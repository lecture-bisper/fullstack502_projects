import React, {useEffect, useState} from 'react';


function Popup({isOpen, onClose, width = '300px', height = '200px', children}) {
    const [show, setShow] = useState(false);
    const [visible, setVisible] = useState(false);


    useEffect(() => {
        if (isOpen) {
            setShow(true);
            setTimeout(() => setVisible(true), 10);
        } else {
            setVisible(false);
            const timer = setTimeout(() => setShow(false), 200);
            return () => clearTimeout(timer);
        }
    }, [isOpen]);

    if (!show) return null;

    return (
        <div
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100vw',
                height: '100vh',
                backgroundColor: 'rgba(0, 0, 0, 0.3)',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                zIndex: 1000,
                opacity: visible ? 1 : 0,
                transition: 'opacity 0.3s ease',
            }}
            onClick={onClose} // 배경 클릭 시 닫기
        >
            <div
                style={{
                    backgroundColor: '#fff',
                    width,
                    height,
                    borderRadius: '12px',
                    padding: '20px',
                    display: 'flex',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                    transform: visible ? 'scale(1)' : 'scale(0.95)', // 확대 애니메이션
                    transition: 'transform 0.3s ease',
                    justifyContent: "flex-start",
                    alignItems: "center",
                    flexDirection: "column",
                }}
                onClick={(e) => e.stopPropagation()} // 내부 클릭 시 닫힘 방지
            >
                {children}
            </div>
        </div>
    );
}

export default Popup;

// Popup 사용법 (팝업이 아닌 배경을 클릭해도 닫힘)
// export default function App() {
//     const [open, setOpen] = useState(false); <- 선언
//
//     return (
//         <div style={{ textAlign: 'center', marginTop: '50px' }}>
//             <CustomButton onClick={() => setOpen(true)}>팝업 열기</CustomButton> <- 버튼이나 클릭 할 수 있는 태그에 onClick={() => setOpen(true)} (버튼을 눌렀을 때 true로 바뀌어 팝업이 나옴)
//
//             <Popup
//                 isOpen={open} onClose={() => setOpen(false)} <- isOpen={open} onClose={() => setOpen(false) 추가
//                 width="400px" <- 너비 지정
//                 height="250px" <- 높이 지정
//             >
//                 <div>
//                     <h2>팝업 내용</h2>
//                     <CustomButton onClick={() => setOpen(false)}>닫기</CustomButton> <- 버튼이나 클릭 할 수 있는 태그에 onClick={() => setOpen(false)} (버튼을 눌렀을 때 false로 바뀌어 팝업이 닫힘
//                 </div>
//             </Popup>
//         </div>
//     );
// }
