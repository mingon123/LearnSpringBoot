$(function(){
	/*------------------------------------------
	 좋아요 읽기 (좋아요 선택 여부와 총개수 표시)
	-------------------------------------------*/
	function selectFav(board_num){
		// 서버와 통신
		$.ajax({
			url:'getFav/' + board_num,
			type:'get',
			dataType:'json',
			success:function(param){
				displayFav(param);
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	}
	
	/*------------------------------------------
	 좋아요등록/삭제
	-------------------------------------------*/
	$('#output_fav').click(function(){
		// 서버와 통신
		$.ajax({
			url:'writeFav',
			type:'post',
			data:JSON.stringify({board_num:$('#output_fav').attr('data-num')}), // json으로 변환해서 전송
			contentType:'application/json;charset=utf-8',
			beforeSend:function(xhr){
				xhr.setRequestHeader($('meta[name="csrf-header"]').attr('content'),
									 $('meta[name="csrf-token"]').attr('content'));
			},
			success:function(param){
				if(param.result=='logout'){
					alert('로그인 후 사용하세요');
				}else if(param.result=='success'){
					displayFav(param);
				}else{
					alert('좋아요 등록/삭제 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	});
		
	/*------------------------------------------
	 좋아요 표시 공통 함수
	-------------------------------------------*/
	function displayFav(param){
		let output;
		if(param.status == 'yesFav'){
			output = '../assets/images/fav02.gif';
		}else if(param.status == 'noFav'){
			output = '../assets/images/fav01.gif';
		}else{
			output = '../assets/images/fav01.gif';
			alert('좋아요 표시 오류 발생');
		}
		// 문서 객체 추가
		$('#output_fav').attr('src',output);
		$('#output_fcount').text(param.count);
	}
	
	/*------------------------------------------
	 초기 데이터 표시
	-------------------------------------------*/
	selectFav($('#output_fav').attr('data-num'));
	
	
});