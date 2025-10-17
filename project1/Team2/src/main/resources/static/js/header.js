const sessionId = userName;  // 세션이 없으면 null 또는 빈 문자열로 바꿔 테스트

window.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.querySelector('a[href="/auth/login"]');
    const logoutBtn = document.querySelector('a[href="/auth/logoutProcess"]');
    const profileBtn = $(".dropdown");
    const userBtn = document.getElementById('userBtn');
    const userMenu = document.getElementById('userMenu');

    if (sessionId && sessionId.trim() !== '') {
        // 세션이 있으면 로그인 버튼 숨김
        loginBtn.style.display = 'none';
    } else {
        // 세션이 없으면 로그아웃 버튼 숨김
        profileBtn.css("display", "none");
        userBtn.addEventListener('click', () => {
            userMenu.style.display = userMenu.style.display === 'none' ? 'block' : 'none';
        });
    }
    // 드랍메뉴 토글
    userBtn.addEventListener('click', () => {
        userMenu.style.display = userMenu.style.display === 'none' ? 'block' : 'none';
    });
    // 토글메뉴 밖을 누르면 닫김
    document.addEventListener('click', (e) => {
        if (!userBtn.contains(e.target) && !userMenu.contains(e.target)) {
            userMenu.style.display = 'none';
        }
    });
});

$("#btn-main-search").on("click", function () {
    const keyword = $("#main-search-keyword").val();
    const type = $("#main-search-type option:selected").val();

    $.ajax({
        url: "/mainSearch",
        type: "GET",
        data: {keyword: keyword},
        success: function () {
            location.href = "/search/" + type;
        }
    })
});