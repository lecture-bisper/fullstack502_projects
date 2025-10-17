import "/src/css/Sheets.css";
import {AgGridReact} from "ag-grid-react";
import {ModuleRegistry, AllCommunityModule} from "ag-grid-community";
import React, {useMemo, useState, useEffect} from "react";

ModuleRegistry.registerModules([AllCommunityModule]);

export default function Sheets({
                                   cols = 3,
                                   rows = 5,
                                   columns,
                                   rowData,
                                   theme = "quartz",
                                   onCellClick,
                                   clickableFields,
                                   loading
                               }) {
    // 컬럼 정의
    const defaultColumns = useMemo(() => {
        const colDefs = columns ?? Array.from({length: cols}, (_, i) => ({
            field: `col${i + 1}`,
            headerName: `컬럼 ${i + 1}`,
        }));

        return colDefs.map((col, idx) => {
                const isEmptyCol = rowData?.every((row) => !row[col.field]);
                return {
                    ...col,
                    flex: col.flex ?? 1,
                    minWidth: col.minWidth ?? 84,
                    // valueGetter: (params) => params.data[col.field] === "" ? null : params.data[col.field],
                    cellClass: (params) => (params.value === null ? "empty-cell" : ""),
                    cellStyle: (params) => {
                        // 외부에서 cellStyle 정의한 게 있으면 우선 사용
                        if (typeof col.cellStyle === "function") {
                            const style = col.cellStyle(params);
                            if (style) return style;
                        } else if (typeof col.cellStyle === "object") {
                            return col.cellStyle;
                        }

                        if (params.value === null || params.value === undefined || params.value === "") return {};
                        if (clickableFields?.includes(col.field)) {
                            return {
                                cursor: "pointer",
                                fontWeight: "600",
                                color: "#0058DB",
                                textDecoration: "underline",
                                textAlign: col.align ?? "left"
                            };
                        }
                        return {textAlign: col.align ?? "left"};
                    },
                    filter: isEmptyCol ? false : col.filter ?? true,
                    comparator: (a, b) => {
                        if (a === null) return 1;
                        if (b === null) return -1;
                        return a > b ? 1 : a < b ? -1 : 0;
                    },
                    resizable: idx !== colDefs.length - 1,
                    cellRenderer: (params) => {
                        if (col.renderCell) {
                            return col.renderCell(params);
                        }
                        if (col.valueFormatter) {
                            return col.valueFormatter(params); // valueFormatter 수동 호출
                        }
                        return params.value;
                    }
                };
            }
        );
    }, [cols, columns, rowData]);

    // 데이터 계산 (빈 행 처리 포함)
    const [computedData, setComputedData] = useState(undefined);
    useEffect(() => {
        if (!Array.isArray(rowData)) {
            setComputedData(undefined);
            return;
        }
        const filledData = rowData ? [...rowData] : [];
        const additionalRows = Math.max(rows - filledData.length, 0);
        const emptyRows = Array.from({length: additionalRows}, () => {
            const row = {};
            for (let c = 0; c < cols; c++) row[`col${c + 1}`] = "";
            return row;
        });

        const realData = filledData.filter((row) => Object.values(row).some((v) => v !== ""));
        const emptyData = filledData.filter((row) => Object.values(row).every((v) => v === ""));
        setComputedData([...realData, ...emptyData, ...emptyRows]);
    }, [rowData, rows, cols]);

    return (
        <div className={`ag-theme-${theme}`} style={{height: "600px", width: "90%", overflowX: "auto"}}>
            <AgGridReact
                domLayout="normal"
                rowData={computedData}
                columnDefs={defaultColumns}
                defaultColDef={{sortable: true, filter: true, minWidth: 120}}
                suppressMovableColumns={true}
                rowSelection={{type: "singleRow"}}
                onCellClicked={(event) => {
                    if (!onCellClick) return;
                    const field = event.colDef?.field ?? event.column?.getColId?.();
                    if (clickableFields && !clickableFields.includes(field)) return;
                    if (event.value === null || event.value === undefined || event.value === "") return;
                    onCellClick(event);
                }}
                loading={loading}
            />
        </div>
    );
}
