// CustomButton.jsx
import React from 'react';
import {BUTTON_COLORS} from '/src/css/BUTTON_COLORS.js';

function CustomButton({
                          onClick,
                          children,
                          textColor = 'white',
                          color = 'deepBlue', // 색상 이름으로 받기
                          width = '90px',
                          height = '35px'
                      }) {
    const bgColor = BUTTON_COLORS[color] || BUTTON_COLORS.deepBlue;

    return (
        <button
            className="custom-btn"
            onClick={onClick}
            style={{
                backgroundColor: bgColor, // 동적 값은 인라인
                width,
                height,
                color: textColor,
            }}
        >
            {children}
        </button>

    );
}

export default CustomButton;


// CustomButton 사용법
// <CustomButton>기본 버튼</CustomButton>
//
// <CustomButton
//     color="blue, deepBlue, red, deepRed, gray 중 택 1"
//     width="250px"
//     height="60px"
//     onClick={() => alert('Clicked!')}
// >
//     핑크 버튼
// </CustomButton>