
const starBox = document.querySelectorAll('.star-rating');
starBox.forEach(function (box) {
    const stars = box.querySelectorAll('.star');
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

    // ⭐ 초기 별점/투표수 화면에 표시
    // function updateRatingInfo(rating, count) {
    //     ratingInfo.textContent = `평점: ${rating.toFixed(1)}, 투표수: ${count}`;
    // }

// 초기 표시
    setRating(currentRating);
    // updateRatingInfo(currentRating, voteCount);

});