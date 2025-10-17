$(() => {

  $(document).ready(function () {

    // movie-list bookmark(보고싶어요) 클릭 시 색상 변경
    $('.bookmark').click(function () {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active')
      }
      else {
        $(this).addClass('active')
      }
    });


    // input[type=search] 커스텀 삭제버튼
    const input = document.getElementById('search-movie');
    const clearBtn = document.getElementById('clearBtn');

    if (input && clearBtn) {
      input.addEventListener('input', () => {
        clearBtn.style.display = input.value ? 'block' : 'none';
      });

      clearBtn.addEventListener('click', () => {
        input.value = '';
        input.focus();
        clearBtn.style.display = 'none';
      });
    }


    // textarea 글자수
    document.querySelectorAll('.pop_con').forEach(group => {
      const textarea = group.querySelector('.contents');
      const charCount = group.querySelector('.charCount');

      // 어떤 팝업은 textarea가 없고(#popup_re_delete), 어떤 팝업은 counter가 없음(#popup_re_update)
      if (!textarea) return;

      // 초기 표시
      if (charCount) charCount.textContent = textarea.value.length;

      textarea.addEventListener('input', () => {
        if (charCount) charCount.textContent = textarea.value.length;
      });
    });



    // 별점 주기
    const rateWrap = document.querySelectorAll('.rating'),
        label = document.querySelectorAll('.rating .rating__label'),
        input2 = document.querySelectorAll('.rating .rating__input'),
        labelLength = label.length,
        opacityHover = '0.6';

    let stars = document.querySelectorAll('.rating .star-icon');

    if (rateWrap.length) {
      checkedRate();

      rateWrap.forEach(wrap => {
        wrap.addEventListener('mouseenter', () => {
          stars = wrap.querySelectorAll('.star-icon');

          stars.forEach((starIcon, idx) => {
            starIcon.addEventListener('mouseenter', () => {
              initStars();
              filledRate(idx, labelLength);

              for (let i = 0; i < stars.length; i++) {
                if (stars[i].classList.contains('filled')) {
                  stars[i].style.opacity = opacityHover;
                }
              }
            });

            starIcon.addEventListener('mouseleave', () => {
              starIcon.style.opacity = '1';
              checkedRate();
            });

            wrap.addEventListener('mouseleave', () => {
              starIcon.style.opacity = '1';
            });
          });
        });
      });
    }


    function filledRate(index, length) {
      if (index <= length) {
        for (let i = 0; i <= index; i++) {
          stars[i].classList.add('filled');
        }
      }
    }

    function checkedRate() {
      let checkedRadio = document.querySelectorAll('.rating input[type="radio"]:checked');


      initStars();
      checkedRadio.forEach(radio => {
        let previousSiblings = prevAll(radio);

        for (let i = 0; i < previousSiblings.length; i++) {
          previousSiblings[i].querySelector('.star-icon').classList.add('filled');
        }

        radio.nextElementSibling.classList.add('filled');


        function prevAll() {
          let radioSiblings = [],
              prevSibling = radio.parentElement.previousElementSibling;

          while (prevSibling) {
            radioSiblings.push(prevSibling);
            prevSibling = prevSibling.previousElementSibling;
          }
          return radioSiblings;
        }
      });

    }

    function initStars() {
      for (let i = 0; i < stars.length; i++) {
        stars[i].classList.remove('filled');
      }
    }


    // 보고싶어요(좋아요)
    $(".bookmark").on('click', function () {

      $.ajax({
        url: "/user/bookmarks/likePost/" + $("#movie-id").val(),
        type: "POST",
        success: function (data) {
        },
        error: function (xhr, textStatus, errorThrown) {
          if (xhr.status == 401) {
            const msg = JSON.parse(xhr.responseText).msg;
            location.href = "/login";
          }
          else {
            alert("에러 발생");
          }
        }
      });
    });


    // header 검색바 (공통 js 에서 실행 안됨)
//     $("#btn-search").on("click", function () {
//       let result = $("#search-movie").val();
//       location.href = "/main/search?searchMovie=" + result;
//     });

  });

// header 검색바
// 버튼으로 검색 실행
//  $(document).on("click", "#btn-search", function () {
//    let result = $("#search-movie").val();
//    location.href = "/main/search?searchMovie=" + result;
//  });

  // 엔터키로 검색 실행
  $(document).on("keydown", "#search-movie", function (e) {
    if (e.key === "Enter") {
      e.preventDefault(); // form 제출 막기
      const result = $(this).val().trim();
      if (result !== "") {
        location.href = "/main/search?searchMovie=" + encodeURIComponent(result);
      }
    }
  });

});