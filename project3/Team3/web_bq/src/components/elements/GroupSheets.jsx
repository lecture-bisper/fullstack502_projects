import {AgGridReact} from "ag-grid-react";
import React, {useEffect, useRef, useState} from "react";
import arrow_img from "/src/assets/sidebar_arrow.png"
import "/src/css/Sheets.css"


function GroupSheets({rowData, columns, groupByField, theme = "quartz", loading}) {
    const [finalData, setFinalData] = useState(undefined)
    const [toggle, setToggle] = useState({});
    const gridRef = useRef();

    // 모드 변경 시 새로고침
    useEffect(() => {
        if (!rowData) return;

        const grouped = groupBy(rowData, groupByField);
        const finalRows = [];
        const newToggle = {};

        for (const [key, items] of Object.entries(grouped)) {
            const sumPrice = items.reduce((acc, item) => acc + (Number(item.totalPrice) || 0), 0);
            const sumQuantity = items.reduce((acc, item) => acc + (Number(item.totalQuantity) || 0), 0);

            finalRows.push(
                {
                    ColId: `group-${key}`,      //고유 id
                    index: <img src={arrow_img} style={{width: "24px", height: "24px"}}
                                alt={"arrow"}/>,
                    itemCode: "-",
                    itemName: "-",
                    categoryId: "-",
                    totalPrice: sumPrice,
                    totalQuantity: sumQuantity,
                    dept: `${key} (${items.length})`,
                    unitTime: `${key} (${items.length})`,
                    lastStorageDate: "-",
                    __isGroup: true,
                    __groupKey: key,
                },
                ...items.map((item, i) => ({
                    ...item,
                    ColId: `${key}-${i}`,
                    index: i + 1,
                    __groupKey: key,
                }))
            );

            newToggle[key] = Object.keys(newToggle).length === 0; // 첫 그룹만 true, 나머지는 false
        }

        // 마지막으로 보여줄 데이터로 저장
        setFinalData(finalRows);
        setToggle(newToggle);
    }, [rowData])

    useEffect(() => {
        if (gridRef.current?.api) {
            gridRef.current.api.resetRowHeights();
        }
    }, [toggle])

    // 컬럼 기능 기본값 지정
    const columnDefs = columns.map((col, idx) => {
        if (col.field === "index") {
            return {
                ...col,
                flex: col.flex || 1,
                resizable: idx !== columns.length - 1,
                cellRenderer: params => {
                    if (params.data?.__isGroup) {
                        const isOpen = toggle[params.data.__groupKey]; // ✅ 토글 상태 확인
                        return (
                            <img
                                src={arrow_img}
                                className={`arrow ${isOpen ? "open" : ""}`}
                                style={{width: "18px", height: "18px"}}
                                alt="arrow"
                            />
                        );
                    }
                    return params.value;
                },
                cellStyle: {textAlign: col.align || "left"},
            };
        }

        return {
            ...col,
            flex: col.flex || 1,
            cellStyle: {textAlign: col.align || "left"},
            sortable: col.sortable !== undefined ? col.sortable : false,
            filter: col.filter !== undefined ? col.filter : false,
            resizable: idx !== columns.length - 1
        };
    });


    // 클릭 핸들러
    const onRowClicked = (params) => {
        if (!params.data.__isGroup) return;
        const key = params.data.__groupKey;
        setToggle(prev => ({...prev, [key]: !prev[key]}));
    };

    // 같은거 끼리 그룹화 하는 함수
    function groupBy(arr, key) {
        return arr.reduce((acc, cur) => {
            const groupKey = cur[key];
            if (!acc[groupKey]) {
                acc[groupKey] = [];
            }
            acc[groupKey].push(cur);
            return acc;
        }, {});
    }

    return (
        <div className={`ag-theme-${theme}`} style={{height: "600px", width: "90%", overflowX: "auto"}}>
            <AgGridReact
                ref={gridRef}
                loading={loading}
                domLayout="normal"
                suppressMovableColumns={true}
                rowSelection={{type: "singleRow"}}
                rowData={finalData}
                columnDefs={columnDefs}
                defaultColDef={{sortable: false, filter: false, minWidth: 120}}
                onRowClicked={onRowClicked}
                getRowHeight={params => {
                    if (params.data.__isGroup) return 42; // 그룹 헤더는 항상 42
                    return toggle[params.data.__groupKey] ? 42 : 0; // 하위 행은 토글에 따라 0
                }}
                getRowClass={params => {
                    if (params.data.__isGroup) return 'group-row';
                    return '';
                }}
            />
        </div>
    );
}

export default GroupSheets;
