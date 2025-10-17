$(document).ready(function () {
    $(".btn-movie-toggle").on("click", function () {
        const type = $(this).data("value");
        const targetDiv = $(this).closest("section").find(".swiper-wrapper");

        $.ajax({
            url: "/api/movie",
            method: "GET",
            data: {type: type},
            success: function (resp) {
                targetDiv.empty();
                resp.forEach(item => {
                    const html = `
                        <div class="swiper-slide">
                            <img alt="${item.original_title}"
                                 src="https://image.tmdb.org/t/p/w300_and_h450_bestv2${item.poster_path}"/>
                            <div class="my-movie-title limit-text">${item.title}</div>
                            <div class="d-flex align-items-center">
                                <div class="my-movie-date">${item.release_date}</div>
                                <div class="star-rating">
                                    <span class="star" data-index="1">★</span>
                                    <span class="star" data-index="2">★</span>
                                    <span class="star" data-index="3">★</span>
                                    <span class="star" data-index="4">★</span>
                                    <span class="star" data-index="5">★</span>
                                </div>
                            </div>
                        </div>`;
                    targetDiv.append(html);  // 원하는 영역에 붙이기
                });

                swiperInstances.forEach((swiper) => {
                    swiper.update();
                });
            },
            error: function () {
                alert("토글 데이터 통신 중 오류")
            }
        })
    });

    $(".btn-tv-toggle").on("click", function () {
        const type = $(this).data("value");
        const targetDiv = $(this).closest("section").find(".swiper-wrapper");

        $.ajax({
            url: "/api/tv",
            method: "GET",
            data: {type: type},
            success: function (resp) {
                targetDiv.empty();
                resp.forEach(item => {
                    const html =`
                        <div class="swiper-slide">
                            <img alt="${item.original_name}"
                                 src="https://image.tmdb.org/t/p/w300_and_h450_bestv2${item.poster_path}"/>
                            <div class="my-movie-title limit-text">${item.name}</div>
                            <div class="d-flex align-items-center">
                                <div class="my-movie-date">${item.first_air_date}</div>
                                <div class="star-rating">
                                    <span class="star" data-index="1">★</span>
                                    <span class="star" data-index="2">★</span>
                                    <span class="star" data-index="3">★</span>
                                    <span class="star" data-index="4">★</span>
                                    <span class="star" data-index="5">★</span>
                                </div>
                            </div>
                        </div>`;
                    targetDiv.append(html);  // 원하는 영역에 붙이기
                });

                swiperInstances.forEach((swiper) => {
                    swiper.update();
                });
            },
            error: function () {
                alert("토글 데이터 통신 중 오류")
            }
        })
    });
});