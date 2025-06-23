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
								<p>${customBrNoHtml(item.re_content)}</p>
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
		let isLiked = item.click_num != 0 && item.click_num == user_num;
		let heartSrc = isLiked ? 'heart02.png' : 'heart01.png';
		
		return `<img class="output-rfav" src="../assets/images/${heartSrc}" data-num="${item.re_num}">
				<span class="output-rfcount">${item.refav_cnt}</span>`;
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
		
		// 입력한 글자수 셋팅
		let inputLength = $('#mre_content').val().length;
		let remain = 300 - inputLength;
		remain += '/300';
		
		// 문서 객체에 반영
		$('#mre_first .letter-count').text(remain);
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
					alert('로그인해야 작성할 수 있습니다.');
				}else if(param.result == 'success'){
					const container = $('#mre_form').parent();
					container.find('p').html(customBrNoHtml($('#mre_content').val()));
					container.find('.modify-date').text('최근 수정일 : 5초미만');
					initModifyForm();
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
	 댓글(답글) 등록, 수정 공통
	===============================*/
	// textarea에 내용 입력시 글자수 체크
	$(document).on('keyup','textarea',function(){
		let inputLength = $(this).val().length;
		if(inputLength > 300){
			$(this).val($(this).val().substring(0,300));
		}

		const remain = `${300 - inputLength}/300`;
		const id = $(this).attr('id');
		
		if(id == 're_content'){
			$('#re_first .letter-count').text(remain);
		}else if(id == 'mre_content'){
			$('#mre_first .letter-count').text(remain);
		}else if(id == 'resp_content'){
			$('#resp_first .letter-count').text(remain);
		}else{
			$('#mresp_first .letter-count').text(remain);
		}
	});
	
	/*==============================
	 댓글 삭제
	===============================*/
	$(document).on('click','.delete-btn',function(){
		let re_num = $(this).attr('data-num');
		// 서버와 통신
		$.ajax({
			url:'deleteReply/' + re_num,
			type:'delete',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));				
			},
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 삭제할 수 있습니다.');
				}else if(param.result == 'success'){
					alert('삭제 완료!');
					fetchReplyList(1);
				}else if(param.result == 'wrongAccess'){
					alert('타인의 글을 삭제할 수 없습니다.');
				}else{
					alert('댓글 삭제 오류 발생');
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
	 댓글 좋아요 등록/삭제
	===============================*/
	$(document).on('click','.output-rfav',function(){
		const heart = $(this);
		
		// 서버와 통신
		$.ajax({
			url:'writeReFav',
			type:'post',
			data:JSON.stringify({re_num:heart.attr('data-num')}),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));				
			},
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인 후 좋아요를 눌러주세요!');
				}else if(param.result == 'success'){
					let newImg = param.status == 'noFav' ? 'heart01.png' : 'heart02.png';
					heart.attr('src',`../assets/images/${newImg}`);
					heart.siblings('.output-rfcount').text(param.count);
				}else{
					alert('댓글 좋아요 등록/삭제 오류');
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
	
	/*==============================
	 * 답글 등록
	 *==============================*/	
	//답글 작성 버튼 클릭시 답글 작성 폼 노출
	$(document).on('click','.response-btn, .response2-btn',function(){
		initResponseForm();//기존 폼 제거
		$(this).hide();//현재 클릭된 버튼만 숨김
		
		let re_num = $(this).attr('data-num');
		let te_parent_num = $(this).attr('data-parent');
		let te_depth = $(this).attr('data-depth');
		console.log(re_num + ',' + te_parent_num 
			                     + ',' + te_depth);
		let responseUI = `
			<form id="resp_form">
				<input type="hidden" name="re_num" id="resp_num" value="${re_num}">
				<input type="hidden" name="te_parent_num" value="${te_parent_num}">
				<input type="hidden" name="te_depth" value="${te_depth}">
				<textarea rows="3" cols="50" name="te_content" id="resp_content" class="rep-content"></textarea>
				<div id="resp_first"><span class="letter-count">300/300</span></div> 					 
				<div id="resp_second" class="align-right">
					<input type="submit" value="답글 작성">
					<input type="button" value="취소" class="resp-reset">
				</div>
			</form>
		`;	
		$(this).after(responseUI);					 
	});
	//답글에서 취소 버큰 클릭시 답글 폼 초기화
	$(document).on('click','.resp-reset',initResponseForm);

	//답글 작성 폼 초기화
	function initResponseForm(){
		$('.response-btn, .response2-btn').show();
		$('#resp_form').remove();
	}
	//답글 등록
	$(document).on('submit','#resp_form',function(event){
		//기본 이벤트 제거
		event.preventDefault();
		const resp_form = $(this);
		
		if($('#resp_content').val().trim()==''){
			alert('내용을 입력하세요!');
			$('#resp_content').val('').focus();
			return
		}
		
		const form_data = $(this).serializeObject();
		const re_num = $('#resp_num').val();
		//서버와 통신
		$.ajax({
			url:'writeResponse',
			type:'post',
			data:JSON.stringify(form_data),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
						$('meta[name="csrf-header"]').attr('content'),
						$('meta[name="csrf-token"]').attr('content'));
			},
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 답글을 작성할 수 있습니다.');
				}else if(param.result == 'success'){
					const btnContainer = resp_form.parents('.sub-item').find('div .rescontent-btn');
					//답글 개수 업데이트
					btnContainer.show();
					btnContainer.attr('data-status',1);
					const newCount = Number(btnContainer.val().substring(5)) + 1;
					btnContainer.val(`▼ 답글 ${newCount}`);
					
					//답글 목록 새로고침
					getListResponse(re_num, resp_form.parents('.sub-item'));
					initResponseForm();
				}else{
					alert('답글 작성 오류 발생');
				}
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					//JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		});
		
	});

	//답글 노출/숨김 버튼 이벤트 처리
	$(document).on('click','.rescontent-btn',function(){
		//data-status의 값이 0이면 답글 미표시 상태 1이면 답글 표시 상태
		if($(this).attr('data-status') == 0){
			//0이면 답글 미표시 상태이므로 답글이 있으면 답글을 표시
			let re_num = $(this).attr('data-num');
			//답글 목록 호출
			getListResponse(re_num,$(this).parent());
			//현재 선택한 내용의 답글 표시 아이콘 토글 처리
			$(this).val($(this).val().replace('▲','▼'));
			$(this).attr('data-status',1);
		}else{
			//현재 선택한 내용의 답글 표시 아이콘 토글 처리
			$(this).val($(this).val().replace('▼','▲'));
			$(this).attr('data-status',0);
			//현재 선택한 내용 삭제
			$(this).parents('.item').find('.respitem').remove();
		}
	});	

	/*==============================
	 * 답글 목록
	 *==============================*/		
	function getListResponse(re_num,responseUI){
		//서버와 통신
		$.ajax({
			url:'listResp/' + re_num,
			type:'get',
			dataType:'json',
			success:function(param){
				responseUI.find('.respitem').remove();
				
				let output = '';
				$(param.list).each(function(index,item){
					let sign_depth = '▶'.repeat(item.te_depth);
					let sign_nick = item.te_parent_num > 0 ?
					 `${sign_depth} ${item.memberVO.parentName}` : '';
					 
					output += `
						<div class="respitem">
							<ul class="detail-info">
								<li class="resp-pro">
									<b>${sign_nick}</b>
									<img src="../member/viewProfile?mem_num=${item.mem_num}" width="30" height="30" class="my-photo">
									<div>
										${item.memberVO.userName}<br>
										<span class="modify-date">${item.te_mdate}</span>
									</div>	
								</li>
							</ul>
							<div class="resp-sub-item">
								<p>${customBrNoHtml(item.te_content)}</p>
								${param.user_num}
								${param.user_num}
							</div>
						</div>
					`; 
				});				
				responseUI.append(output);				
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					//JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		});
	}
	
	/*==============================
	 초기 데이터(목록) 호출
	===============================*/
	fetchReplyList(1);

});