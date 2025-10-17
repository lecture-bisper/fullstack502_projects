import { memo, useMemo } from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend,Colors } from "chart.js";
import {asWon, nfKR} from "../pages/statistics/StaticsConfig.js";

// Chart.js에서 원형 조각(arc)을 그리는 기본 요소
ChartJS.register(ArcElement, Tooltip, Legend, Colors);

/**
 * 재사용 가능한 Pie 차트 컴포넌트
 * props:
 * - labels: string[]                (조각 이름)
 * - values: number[]                (값)
 * - height?: number                 (기본 320)
 * - onSliceClick?: (payload) => void (조각 클릭 시 {index, label, value})
 * - tooltipFormatter?: (value, label) => string
 * - legend?: "right" | "bottom" | false
 */

function ChartPie({
                      labels = [],
                      values = [],
                      height = 320,
                      onSliceClick,
                      legend = "right",
                  }) {
    const data = useMemo(
        () => ({
            labels,
            datasets: [{
                data: values
            }],
        }),
        [labels, values]
    );

    const palette = [
        "#276EF1","#12B886","#FF8C42","#845EF7","#F03E3E",
        "#FFD43B","#0CA678","#228BE6","#FFA94D","#868E96"
    ];

    const options = useMemo(
        () => ({
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                colors: { forceOverride: true },
                legend: {
                    position: "right", // or "bottom"
                    labels: {
                        // ▷ 색상칩 박스 크기
                        boxWidth: 30,   // 기본 40, 값 키우면 가로로 더 큼
                        boxHeight: 20,  // v4부터 지원, 박스 높이 조절

                        // ▷ 텍스트 스타일
                        font: {
                            size: 16,     // 기본 12 → 크게
                        },
                        color: "#333",   // 텍스트 색

                        // ▷ 범례 항목 사이 여백
                        padding: 20,    // 박스+텍스트 묶음 사이 간격
                    }
                },
                tooltip: {
                    backgroundColor: "#FFF9C4", // 배경 제거
                    titleColor: "#333",
                    bodyColor: "#333",
                    borderWidth: 0,
                    callbacks: {
                        label: (ctx) => {
                            const label = ctx.label || "";
                            const value = ctx.parsed;
                            const dataset = ctx.dataset.data;
                            const total = dataset.reduce((a, b) => a + b, 0);
                            const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            // 🔹 라벨 / 값(₩) / 퍼센트 표시
                            return `${label} / ${asWon(value)} / ${percent}%`;
                        },
                    },
                },
            },
            onClick: (evt, elements) => {
                if (!elements?.length || !onSliceClick) return;
                const idx = elements[0].index;
                onSliceClick({ index: idx, label: labels[idx], value: values[idx] });
            },
        }),
        [labels, values, onSliceClick, legend]
    );


    if (!labels.length) {
        return (
            <div style={{ height, display: "grid", placeItems: "center", color: "#888" }}>
                데이터가 없습니다
            </div>
        );
    }

    return <div style={{ height }}><Pie data={data} options={options} /></div>;
}

export default memo(ChartPie);
