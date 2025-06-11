$(function(){
	/*--------------------------
	 회원가입 : ID/별명 중복체크
	---------------------------*/
	// 아이디 중복체크
	// 아이디 중복 여부 저장 변수 : 0->아이디 중복 또는 중복체크 미실행
	//							  1->아이디 미중복
	let checkId = 0;
	// 별명 중복 여부 저장 변수 : 0->별명 중복 또는 중복체크 미실행
	//							1->별명 미중복
	let checkNick = 0;
	
	// 아이디 중복 체크
	$('#confirm_id').click(function(){
		if($('#id').val().trim()==''){
			$('#message_id').css('color','red').text('아이디를 입력하세요');
			$('#id').val('').focus();
			return;
		}
		
		// 서버와 통신
		$.ajax({
			url:'confirmId/' + $('#id').val(),
			type:'get',
			dataType:'json',
			success:function(param){
				if(param.result=='idNotFound'){
					checkId = 1;
					$('#message_id').css('color','#000').text('등록 가능 ID');
				}else if(param.result == 'idDuplicated'){
					checkId = 0;
					$('#message_id').css('color','red').text('중복된 ID');
					$('#id').val('').focus();					
				}else if(param.result == 'notMatchPattern'){
					checkId = 0;
					$('#message_id').css('color','red').text('영문,숫자 4~12자 입력');
					$('#id').val('').focus();
				}else{
					checkId = 0;
					alert('아이디 중복 체크 오류');
				}
			},
			error:function(){
				checkId = 0;
				alert('네트워크 오류 발생');
			}
			
		});
	}); // end of click
	
	// 별명 중복체크
	$('#confirm_nick').click(function(){
		if($('#nick_name').val().trim()==''){
			$('#message_nick').css('color','red').text('별명을 입력하세요');
			$('#nick_name').val('').focus();
			return;
		}
		
		// 서버와 통신
		$.ajax({
			url:'confirmNickName/' + $('#nick_name').val(),
			type:'get',
			dataType:'json',
			success:function(param){
				if(param.result=='nickNotFound'){
					checkNick = 1;
					$('#message_nick').css('color','#000').text('등록 가능 별명');
				}else if(param.result == 'nickDuplicated'){
					checkNick = 0;
					$('#message_nick').css('color','red').text('중복된 별명');
					$('#nick_name').val('').focus();					
				}else if(param.result == 'notMatchPattern'){
					checkNick = 0;
					$('#message_nick').css('color','red').text('한글,영문,숫자 2~10자 입력');
					$('#nick_name').val('').focus();
				}else{
					checkNick = 0;
					alert('별명 중복 체크 오류');
				}
			},
			error:function(){
				checkNick = 0;
				alert('네트워크 오류 발생');
			}
			
		});
	}); // end of click
	
	// 별명 입력 텍스트에 따라서 입력창 크기 및 중복체크 버튼 표시 여부 제어
	$('#nick_name').keyup(function(){
		if($('#nick_name').val().length > 0){
			// 별명을 입력한 경우
			$('#confirm_nick').show();
			$('#nick_name').css('width',283);
			
		}else{
			// 별명을 입력하지 않은 경우
			$('#confirm_nick').hide();
			$('#nick_name').css('width',350);
			$('#message_nick').text('');
		}
	});
	
});