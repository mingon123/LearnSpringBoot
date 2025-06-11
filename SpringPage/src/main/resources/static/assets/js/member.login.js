$(function(){
	/*----------------------
	 회원로그인
	-----------------------*/
	$('#member_login').submit(function(){
		// 메시지 초기화
		$('#error_id,#error_passwd,.error_invalid').text('');
		
		if($('#id').val().trim()=='' && $('#passwd').val().trim()==''){ // 둘다 미입력
			$('#error_id').text('아이디를 입력하세요').slideDown(500);
			$('#error_passwd').text('비밀번호를 입력하세요').slideDown(500);
			return false;
		}
		if($('#id').val().trim()=='' && $('#passwd').val().trim()!=''){ // 비밀번호만 입력
			$('#error_id').text('아이디를 입력하세요').slideDown(500);
			return false;
		}
		if($('#id').val().trim()!='' && $('#passwd').val().trim()==''){ // 아이디만 입력
			$('#error_passwd').text('비밀번호를 입력하세요').slideDown(500);
			return false;
		}
	}); // end of submit
	
	$('#id').keydown(function(){
		$('#error_id, .error-invalid').slideUp(1000);
	});
	
	$('#passwd').keydown(function(){
		$('#error_passwd, .error-invalid').slideUp(1000);
	})
	
});