loadStar();

function loadStar() {
    const starBox = document.querySelectorAll('.star-rating1');
    starBox.forEach(function (box) {
        const stars = box.querySelectorAll('.star1');
        let currentRating = box.dataset.currentRating / 2;  // 초기 평균 평점
        let voteCount = box.dataset.voteCount;      // 투표 수

        // 별점 화면에 표시 함수
        function setRating(rating) {
            stars.forEach((star, index) => {
                star.classList.remove('full', 'half');
                const starValue = index + 1;
                if (rating >= starValue) {
                    star.classList.add('full');
                } else if (rating >= starValue - 0.5) {
                    star.classList.add('half');
                }
            });
        }

        // 초기 표시
        setRating(currentRating);
    });
}