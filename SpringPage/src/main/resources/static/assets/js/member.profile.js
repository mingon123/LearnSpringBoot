$(function(){
	// MY페이지 프로필사진 등록 및 수정
	// 수정 버튼 이벤트 처리
	$('#photo_btn').click(function(){
		$('#upload').trigger('click');
	})
	// 업로드 이미지 미리보기
	// 파일 업로드 태그의 선택자, 업로드되는 이미지가 보여지는 태그 선택자, 업로드 허용 확장자, 업로드 허용 파일 사이즈
	customViewImage('#upload','.my-photo',['png','jpg','jpeg','gif'],1024*1024);
	
	// 파일 전송 이벤트 처리
	$('#photo_submit').click(function(){
		if($('#upload').val()==''){
			alert('파일을 선택하세요!');
			return;
		}
		// 파일 정보 읽기
		const form_data = new FormData();
		form_data.append('upload',$('#upload')[0].files[0]); // 배열로 인식하기 때문에 하나밖에 없을 때는 0으로 해서 값 읽어오도록 처리
		// 서버와 통신
		$.ajax({
			url:'../member/updateMyPhoto',
			type:'put',
			data:form_data, // 변수를 그대로 값에 넣기 때문에 ''넣으면 안됨
			dataType:'json',
			contentType:false, // 파일이 포함되어 있으면 추가
			processData:false,
			beforeSend:function(xhr){ // 토큰처리. thymeleaf는 자동설정이 되지만 put 방식은 csrf 자동처리 안됨
				xhr.setRequestHeader($('meta[name="csrf-header"]').attr('content'),
									 $('meta[name="csrf-token"]').attr('content')); // layout_mypage.html에서 설정한 meta 태그에 넣은 값 호출
			},
			success:function(param){
				if(param.result=='logout'){
					alert('로그인 후 사용하세요');
				}else if(param.result=='success'){
					alert('프로필 사진이 수정되었습니다.');
					// 파일 업로드 태그의 선택자,업로드되는 이미지가 보여지는 태그 선택자
					customInitImage('#upload','.my-photo'); // customjs.js의 함수
					$('#photo_choice').hide();
					$('#photo_btn').show();
				}else{
					alert('파일 전송 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	});
	
	// 취소 버튼 처리
	$('#photo_reset').click(function(){
		// 파일 업로드 태그의 선택자, 업로드되는 이미지가 보여지는 태그 선택자
		customCancelImage('#upload','.my-photo'); // customjs.js에서 만든 메서드
		$('#photo_choice').hide();
		$('#photo_btn').show();
	});
	
});