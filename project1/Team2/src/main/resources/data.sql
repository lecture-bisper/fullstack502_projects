-- 장르
INSERT INTO genre (id, name, kr_name)
VALUES (28, 'Action', '액션'),
       (12, 'Adventure', '어드벤처'),
       (16, 'Animation', '애니메이션'),
       (35, 'Comedy', '코미디'),
       (80, 'Crime', '범죄'),
       (99, 'Documentary', '다큐멘터리'),
       (18, 'Drama', '드라마'),
       (10751, 'Family', '가족'),
       (14, 'Fantasy', '판타지'),
       (36, 'History', '역사'),
       (27, 'Horror', '공포'),
       (10402, 'Music', '음악'),
       (9648, 'Mystery', '미스터리'),
       (10749, 'Romance', '로맨스'),
       (878, 'Science Fiction', '공상과학'),
       (10770, 'TV Movie', 'TV 영화'),
       (53, 'Thriller', '스릴러'),
       (10752, 'War', '전쟁'),
       (37, 'Western', '서부극'),
       (10759, 'Action & Adventure', '액션 & 어드벤처'),
       (10762, 'Kids', '어린이'),
       (10763, 'News', '뉴스'),
       (10764, 'Reality', '리얼리티'),
       (10765, 'Sci-Fi & Fantasy', 'SF & 판타지'),
       (10766, 'Soap', '연속극'),
       (10767, 'Talk', '토크쇼'),
       (10768, 'War & Politics', '전쟁 & 정치');


-- 유저
INSERT INTO user (id, password, name, email, birth_date, gender, profile_img, create_date, admin)
VALUES ('team2', 'qwe123', '팀2', 'team2@gmail.com', '2025-03-28', 'M', '/images/profile_male2.png', '2025-07-18', 'N'),
       ('team1', 'qwe123', '팀1', 'team1@gmail.com', '2002-12-20', 'M', '/images/profile_male4.png', '2025-06-17', 'N'),
       ('team3', 'qwe123', '팀3', 'team3@naver.com', '2003-01-01', 'W', '/images/profile_male4.png', '2025-07-24', 'N'),
       ('team4', 'qwe123', '팀4', 'team4@gmail.com', '2004-06-15', 'M', '/images/profile_male4.png', '2025-01-11', 'N');

-- 선호 장르
INSERT INTO prefer_genre (user_id, genre_id, type)
VALUES ('team2', 14, 'movie'),
       ('team2', 878, 'movie'),
       ('team2', 16, 'movie'),
       ('team2', 16, 'tv');

-- 북마크
INSERT INTO favorites (user_id, contents_id, type)
VALUES ('team2', 1061474, 'movie'),
       ('team2', 1425045, 'movie'),
       ('team2', 4614, 'tv');

-- 댓글
INSERT INTO comments (user_id, type, content_id, comment_date, comment, rating, user_name)
VALUES ('team2', 'movie', '1061474', '2025-07-11', '너무 재밌어요!', 7, '팀2'),
       ('team1', 'movie', '1061474', '2025-07-12', '다음 편이 기다려져요~', 6, '팀1'),
       ('team2', 'movie', '1061474', '2025-07-13', '연기력이 정말 대단하네요!', 5, '팀2'),
       ('team3', 'movie', '1061474', '2025-07-14', '스토리가 예상 밖이라 신기해요!', 4, '팀3'),
       ('team1', 'movie', '1061474', '2025-07-15', 'OST가 마음에 들어요', 3, '팀1');
