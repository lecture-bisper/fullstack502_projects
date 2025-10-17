var swiper = new Swiper(".mySwiper", {
    slidesPerView: 3,        // 기본값 (lg 이상에서 유지할 수도 있어요)
    spaceBetween: 30,
    centeredSlides: true,
    loop: true,
    autoplay: {
        delay: 1500,
        disableOnInteraction: false,
        pauseOnMouseEnter: true,
    },
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    },
    navigation: {
        nextEl: ".swiper-button-next",
        prevEl: ".swiper-button-prev",
    },

    breakpoints: {
        1400: {  // xxl 이상: 기본값 유지 (3, 30)
            slidesPerView: 3,
            spaceBetween: 30,
        },
        1200: {  // xl 이상: 기본값 유지 (3, 30)
            slidesPerView: 3,
            spaceBetween: 30,
        },
        992: {   // lg 이상: 기본값 유지 (3, 30)
            slidesPerView: 3,
            spaceBetween: 30,
        },
        768: {   // md 이상: 슬라이드 수 줄임
            slidesPerView: 2,
            spaceBetween: 20,
        },
        576: {   // sm 이상
            slidesPerView: 1,
            spaceBetween: 15,
        },
        0: {     // xs
            slidesPerView: 1,
            spaceBetween: 10,
        }
    }
});