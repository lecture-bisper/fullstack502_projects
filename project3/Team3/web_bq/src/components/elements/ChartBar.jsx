import { memo, useMemo } from "react";
import { Bar } from "react-chartjs-2";
import {
    Chart as ChartJS,
    BarElement,
    CategoryScale,
    LinearScale,
    Tooltip,
    Legend,
} from "chart.js";
import {asWon, nfKR} from "../pages/statistics/StaticsConfig.js";

// BarElement → 막대를 그리는 요소(Element)
// CategoryScale → 문자열 라벨용 X축 스케일(예: "1월", "2월"…)
// LinearScale → 숫자용 Y축 스케일
// Tooltip → 툴팁 플러그인(마우스 올리면 뭐 뜨는거)
// Legend → 범례 플러그인(차트에 표시된 데이터셋(또는 시리즈)의 목록. 색상 점 + 라벨로 어떤 데이터가 어떤 색인지 안내.)
ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend);

/**
 * 재사용 가능한 Bar 차트 컴포넌트
 * props:
 * - labels: string[]           (x축 라벨)
 * - values: number[]           (y축 값)
 * - title?: string             (dataset 라벨)
 * - height?: number            (기본 320)
 * - onBarClick?: (payload) => void  (막대 클릭 시 호출; {index, label, value})
 * - tooltipFormatter?: (value, label) => string
 */

function ChartBar({
                      labels = [],
                      values = [],
                      title = "사용금액",
                      height = 320,
                      onBarClick,
                      tooltipFormatter = (v, l) => asWon(v),
                  }) {
    const data = useMemo(
        () => ({
            labels,
            datasets: [
                {
                    label: title,
                    data: values,
                    backgroundColor: "rgba(54, 162, 235, 0.6)",   // 막대 색
                    borderColor: "rgba(54, 162, 235, 1)",         // 테두리 색
                    borderWidth: 1,
                    maxBarThickness: 40,
                },
            ],
        }),
        [labels, values, title]
    );

    const options = useMemo(
        () => ({
            responsive: true,
            maintainAspectRatio: false,
            layout: { padding: 0 },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (ctx) => tooltipFormatter(ctx.parsed.y, ctx.label),
                    },
                },
            },
            scales: {
                x: {
                    type: "category",
                    alignToPixels: true, // 픽셀 정렬
                    grid: { drawBorder: true },
                    ticks: {
                        // 예: "2025-01" → "1월"
                        callback: (val, idx) => {
                            const l = labels[idx] ?? "";
                            return /^\d{4}-\d{2}$/.test(l) ? `${Number(l.slice(5))}월` : l;
                        },
                    },
                },
                y: {
                    beginAtZero: true,
                    offset: false,       // 상하 여백 제거
                    alignToPixels: true,
                    grid: { drawBorder: true },
                    ticks: { callback: (v) => nfKR.format(v) },
                    // 필요하면 아래처럼 꽉 채우기
                },
            },
            onClick: (evt, elements) => {
                if (!elements?.length || !onBarClick) return;
                const index = elements[0].index;
                onBarClick({ index, label: labels[index], value: values[index] });
            },
        }),
        [labels, onBarClick, tooltipFormatter] // values가 ticks 콜백에 직접 안 쓰이니 생략 가능
    );

    if (!labels.length) {
        return (
            <div style={{ height, display: "grid", placeItems: "center", color: "white" }}>
                데이터가 없습니다
            </div>
        );
    }

    return <div style={{ height }}><Bar data={data} options={options} /></div>;
}

export default memo(ChartBar);
