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
	 초기 데이터(목록) 호출
	===============================*/
	fetchReplyList(1);

});