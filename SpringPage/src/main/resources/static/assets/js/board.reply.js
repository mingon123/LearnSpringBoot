$(function(){
	/*==============================
	 식별자 정의
	===============================*/
	let rowCount = 10; // 한 페이지에 보여줄 댓글수
	let currentPage; // 현재 댓글 페이지 번호
	let count; // 전체 댓글수
	
	/*==============================
	 댓글 목록
	===============================*/
	// 댓글 목록
	function fetchReplyList(pageNum){
		currentPage = pageNum;
		// 서버와 통신
		$.ajax({
			url:`listReply/${$('#board_num').val()}/${pageNum}/${rowCount}`,
			type:'get',
			dataType:'json',
			beforeSend:function(){
				$('#loading').show(); // 로딩 이미지 표시
			},
			complete:function(){
				$('#loading').hide(); // 로딩 이미지 숨김
			},
			success:function(param){
				count = param.count;
				
				if(pageNum == 1){
					// 첫 페이지일 경우 기존 댓글 제거
					$('#output').empty();
				}
				// 댓글수 표시
				updateReplyCount(count);
				// 댓글 목록 렌더링
				renderReplyList(param.list,param.user_num);
				// 페이징 버튼 처리
				if(currentPage >= Math.ceil(count/rowCount)){
					$('.paging-button').hide();
				}else{
					$('.paging-button').show();
				}
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					// JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		});
	}
	
	// 댓글 수 표시
	function updateReplyCount(count){
		$('#output_rcount').text('댓글수('+count+')');
	}
	// 댓글 목록 화면 제어
	function renderReplyList(list,user_num){
		$(list).each(function(index,item){
			if(index > 0){
				$('#output').append('<hr size="1" width="100%">');
			}
			
			let output = `
						<div class="item">
							<ul class="detail-info">
								<li>
									<img src="../member/viewProfile?mem_num=${item.mem_num}" width="40" height="40" class="my-photo">
								</li>
								<li>
									${item.memberVO.userName}<br>
									<span class="modify-date">${item.re_mdate ? '최근 수정일 : ' + item.re_mdate : '등록일 : ' + item.re_date}</span>
								</li>
							</ul>
							<div class="sub-item">
								<p>${item.re_content}</p>
								${renderLike(item,user_num)}
								${renderButtons(item,user_num)}
							</div>
						</div>
			`
			$('#output').append(output);
		})
	}
	// 좋아요 표시
	function renderLike(item,user_num){
		return '별점';
	}
	// 버튼 표시
	function renderButtons(item,user_num){
		let buttons = '';
		
		if(user_num == item.mem_num){
			buttons += `
					<input type="button" data-num="${item.re_num}" value="수정" class="modify-btn">
					<input type="button" data-num="${item.re_num}" value="삭제" class="delete-btn">
			`;
		}
		
		if(user_num){ 
			buttons += ` 
					<input type="button" data-num="${item.re_num}" data-parent="0" data-depth="0" value="답글 작성" class="response-btn">
			`;
		}
		
		if(item.resp_cnt > 0){
			buttons += `
					<div>
						<input type="button" data-status="0" data-num="${item.re_num}" value="▲답글 ${item.resp+cnt}" class="rescontent-btn">
					</div>
			`
		}else{
			buttons += `
					<div>
						<input type="button" data-status="0" data-num="${item.re_num}" value="▲답글 0" class="rescontent-btn" style="display:none;">
					</div>
			`
		}
		return buttons;
	}
	
	/*==============================
	 댓글 등록
	===============================*/
	// 댓글 등록
	$('#re_form').submit(function(event){
		// 기본 이벤트 제거
		event.preventDefault();
		
		if($('#re_content').val().trim()==''){
			alert('내용을 입력하세요');
			$('#re_content').val('').focus();
			return;
		}
	
		const formData = $(this).serializeObject();
		
		// 서버와 통신
		$.ajax({
			url:'writeReply',
			type:'post',
			data:JSON.stringify(formData),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));				
			},
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 작성할 수 있습니다.')
				}else if(param.result == 'success'){
					resetReplyForm();
					fetchReplyList(1);
				}else{
					alert('댓글 등록 오류 발생');
				}
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					// JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		})
	});
	
	// 댓글 작성 폼 초기화
	function resetReplyForm(){
		$('textarea').val('');
		$('#re_first .letter-count').text('300/300');
	}
	
	/*==============================
	 댓글 수정
	===============================*/
	// 댓글 수정 버튼 클릭시 수정 폼 노출
	$(document).on('click','.modify-btn',function(){
		let re_num = $(this).attr('data-num');
		let re_content = $(this).parent().find('p').html().replace(/<br>/gi,'\r\n');
		let modifyFormHTML = `
				<form id="mre_form">
					<input type="hidden" name="re_num" value="${re_num}">
					<textarea rows="3" cols="50" name="re_content" id="mre_content" class="rep-content">${re_content}</textarea>
					<div id="mre_first">
						<span class="letter-count">300/300</span>
					</div>
					<div id="mre_second" class="align-right">
						<input type="submit" value="수정">
						<input type="button" value="취소" class="re-reset">
					</div>
					<hr size="1" noshade width="96%">
				</form>
		`;
		initModifyForm(); // 기존 수정 폼 제거
		$(this).parent().hide(); // 원래 내용(sub-item) 감춤
		// 수정 폼 삽입
		$(this).parents('.item').append(modifyFormHTML);
	});
	// 수정폼에서 취소 버튼 클릭시 수정 폼 초기화
	$(document).on('click','.re-reset',initModifyForm);
	
	// 댓글 수정 폼 초기화
	function initModifyForm(){
		$('.sub-item').show();
		$('#mre_form').remove();
	}
	// 댓글 수정
	$(document).on('submit','#mre_form',function(event){
		// 기본 이벤트 제거
		event.preventDefault();
	
		if($('#mre_content').val().trim()==''){
			alert('내용을 입력하세요');
			$('#mre_content').val('').focus();
			return; // 기본 이벤트를 제거했기 때문에 return false 아닌 return만 해도 됨.
		}
		
		const formData = $(this).serializeObject();
		
		// 서버와 통신
		$.ajax({
			url:'updateReply',
			type:'put',
			data:JSON.stringify(formData),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));				
			},
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 작성할 수 있습니다.')
				}else if(param.result == 'success'){
					resetReplyForm();
					fetchReplyList(1);
				}else if(param.result == 'wrongAccess'){
					alert('타인의 글을 수정할 수 없습니다');
				}else{
					alert('댓글 수정 오류 발생');
				}
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					// JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		});
		
	});
	
	/*==============================
	 초기 데이터(목록) 호출
	===============================*/
	fetchReplyList(1);

});