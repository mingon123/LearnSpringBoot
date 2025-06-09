$(function(){
	/*----------------------------
	 목록
	---------------------------- */
	function selectList(){
		// 서버와 통신
		$.ajax({
			url:'list',
			type:'get',
			dataType:'json',
			success:function(param){
				$('#list').empty(); // 내용 비우기
				
				$(param).each(function(index,item){ // 각각의 정보를 확인
					let output = '';
					if(item.completed){ // 할 일 체크가 1이면 true
						output += `
								<li class="list-group-item list-group-item-success">
									<span class="pointer todo-done check-btn" id="${item.id}" data-check="${item.completed}">${item.todo} (완료)</span>
						`;
					}else{ // 할 일 체크가 0이면 false 
						output += `
								<li class="list-group-item">
									<span class="pointer check-btn" id="${item.id}" data-check="${item.completed}">${item.todo} (작성일:${item.created})</span>
						`;
					}
					output += `
							<span class="float-end badge bg-secondary pointer delete-btn" id="${item.id}">삭제</span>
							</li>
					`;
					$('#list').append(output);
				});
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	}

	
	/*----------------------------
	 등록
	---------------------------- */
	function addTodo(){
		if($('#todo').val().trim()==''){
			alert('할 일을 입력하세요');
			$('#todo').val('').focus();
			return;
		}
		
		// 서버와 통신
		$.ajax({
			url:'write', // 확장자(.do) 생략
			type:'post', // 데이터 생성(처리)은 POST
			data:JSON.stringify({todo:$('#todo').val()}), // 파라미터네임과 value의 쌍이 아닌 json형태로 바꿔서 보냄
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			success:function(param){
				if(param.result=='success'){
					// 할 일 등록 성공
					// 입력창 초기화
					$('#todo').val('');
					// 목록 호출
					selectList();
				}else{
					// 할 일 등록 실패
					alert('할 일 등록 오류 발생')
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	}
	
	/*----------------------------
	 엔터 키 이벤트 연결
	---------------------------- */
	$('#todo').keydown(function(event){
		if(event.keyCode == 13){
			addTodo();
		}
	});
	
	/*----------------------------
	 등록 버튼 이벤트 연결
	---------------------------- */
	$('#add_btn').click(function(){
		addTodo();
	});
	
	/*----------------------------
	 할 일 체크 이벤트 영역
	---------------------------- */
	$(document).on('click','.check-btn',function(){ // class이기 때문에 .check-btn
		// 서버와 통신
		$.ajax({
			url:'update',
			type:'put',
			data:JSON.stringify({id:$(this).attr('id'),completed:$(this).attr('data-check')}),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			success:function(param){
				if(param.result == 'success'){
					// 목록 호출
					selectList();
				}else{
					alert('할 일 체크 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	});
	
	/*----------------------------
	 삭제 이벤트 연결
	---------------------------- */
	$(document).on('click','.delete-btn',function(){
		// 서버와 통신
		$.ajax({
			url:'delete/' + $(this).attr('id'), // 데이터가 주소에 있기 때문에 contentType 등 입력안해도 됨
			type:'delete',
			dataType:'json',
			success:function(param){
				if(param.result == 'success'){					
					// 목록 호출
					selectList();
				}else{
					alert('할 일 삭제 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	});
			
	/*----------------------------
	 초기 데이터 설정
	---------------------------- */
	selectList();
});