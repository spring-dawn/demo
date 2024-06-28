import React, { useEffect, useState, useRef } from "react";
import { AgGridReact } from "ag-grid-react";
import { Button, Tooltip } from "antd";
import * as FileSaver from "file-saver";
import * as XLSX from "xlsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowDown, faArrowUp } from "@fortawesome/free-solid-svg-icons";

function Table1({ gugun, data, dong, handleClick, clickTable }) {
    const gridApi = useRef(null);
    const [filterData, setFilterData] = useState(null);

    // 드래그 데이터 필터링
    useEffect(() => {
        if (gridApi?.current?.api && Array.isArray(clickTable)) {
            gridApi.current.api.forEachNode((node) => {
                node.setSelected(false);

                clickTable.forEach((ele) => {
                    if (node.data.key == ele) {
                        node.setSelected(true);
                    }
                });
            });
        }
    }, [gridApi, clickTable]);

    const unit = {
        pop: "명",
        households: "가구",
        vehicleCnt: "대",
        emptyLands: "개소",
        emptyArea: "㎡",
        pfTotal: "면",
        pfRDSum: "면",
        pfOutSum: "면",
        pfSubSum: "면",
        pdTotal: "대",
        pdRDSum: "대",
        pdOutSum: "대",
        pdSubSum: "대",
        PK1: "%",
        PK2: "%",
        PK3: "%",
        PK7: "%",
        PK9: "%",
    };

    const CustomHeaderComponent = (props) => {
        const currentSort = props.column.getSort();

        return (
            <Tooltip
                placement="top"
                title={
                    <div
                        style={{ color: "black", fontWeight: 800, pointerEvents: "none", overflow: "hidden" }}
                    >{`단위 : ${unit[props.column.colId]}`}</div>
                }
                color={"white"}
            >
                <div
                    style={{ display: "flex", alignItems: "center" }}
                    onClick={(event) => {
                        if (!currentSort) {
                            props.setSort("asc", event.shiftKey);
                        } else if (currentSort == "asc") {
                            props.setSort("desc", event.shiftKey);
                        } else if (currentSort == "desc") {
                            props.setSort(undefined, event.shiftKey);
                        }
                    }}
                >
                    {props.displayName}
                    <div className="sort_wrap" style={{ marginLeft: 6 }}>
                        {currentSort == "asc" && <FontAwesomeIcon icon={faArrowUp} />}
                        {currentSort == "desc" && <FontAwesomeIcon icon={faArrowDown} />}
                    </div>
                </div>
            </Tooltip>
        );
    };

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
        wrapHeaderText: true,
    };

    const columnDefs = [
        {
            field: "seq",
            headerName: "번호",
            sortable: true,
            width: "90",
        },
        {
            field: "key",
            headerName: "구분",
            sortable: true,
            width: "100",
        },
        {
            field: "pop",
            headerName: "인구",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "100",
            headerComponent: CustomHeaderComponent,
            headerComponentParams: {
                sortable: true,
                enableSorting: true,
            },
        },
        {
            field: "households",
            headerName: "가구수",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "100",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "vehicleCnt",
            headerName: "차량등록대수",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "100",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "emptyLands",
            headerName: "빈터(공한지) 개소",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "120",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "emptyArea",
            headerName: "빈터(공한지) 면적",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "120",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pfTotal",
            headerName: "주차시설(합계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pfRDSum",
            headerName: "노상(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pfOutSum",
            headerName: "노외(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pfSubSum",
            headerName: "부설(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pdTotal",
            headerName: "주차수요(합계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pdRDSum",
            headerName: "노상(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pdOutSum",
            headerName: "노외(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "pdSubSum",
            headerName: "부설(소계)",
            valueFormatter: ({ value }) => value.toLocaleString(),
            sortable: true,
            width: "110",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "PK1",
            headerName: "주차장확보율",
            sortable: true,
            width: "90",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "PK2",
            headerName: "주차장과부족",
            sortable: true,
            width: "90",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "PK3",
            headerName: "주차장이용률",
            sortable: true,
            width: "90",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "PK7",
            headerName: "불법주차율",
            sortable: true,
            width: "120",
            headerComponentFramework: CustomHeaderComponent,
        },
        {
            field: "PK9",
            headerName: "개방여력",
            sortable: true,
            width: "100",
            headerComponentFramework: CustomHeaderComponent,
        },
    ];

    const extractData = (rowData, columnDefs) => {
        return columnDefs.map((column) => rowData[column.field]);
    };

    const excelDownload = (columnDefs, data) => {
        const excelFileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        const excelFileExtension = ".xlsx";
        const excelFileName = "TEST";

        const wsData = [columnDefs.map((column) => column.headerName)];

        if (filterData) {
            filterData.forEach((row) => {
                wsData.push(extractData(row, columnDefs));
            });
        } else {
            data.forEach((row) => {
                wsData.push(extractData(row, columnDefs));
            });
        }

        const ws = XLSX.utils.aoa_to_sheet(wsData);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Sheet1");

        const excelButter = XLSX.write(wb, { bookType: "xlsx", type: "array" });
        const excelFile = new Blob([excelButter], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });
        FileSaver.saveAs(excelFile, "통계데이터.xlsx");
    };

    const handleFilterClick = () => {
        const filteredRows = data.filter((row) => clickTable.includes(row.key));
        setFilterData(filteredRows);
    };

    return (
        <>
            <div className="ag-theme-alpine" style={{ width: "100%", height: "85%" }}>
                <div className="button">
                    <div className={`btn_1 ${clickTable.length > 0 ? "active" : ""}`} onClick={handleFilterClick}>
                        필터링
                    </div>

                    <div
                        className="btn_2"
                        onClick={() => {
                            setFilterData(null);
                        }}
                    >
                        초기화
                    </div>
                    <div
                        className="btn_3"
                        onClick={() => {
                            excelDownload(columnDefs, data);
                        }}
                    >
                        Excel
                    </div>
                </div>
                <AgGridReact
                    ref={gridApi}
                    headerHeight={60}
                    rowData={filterData ? filterData : data}
                    columnDefs={columnDefs}
                    tooltipShowDelay={0}
                    tooltipHideDelay={2000}
                    defaultColDef={defaultColDef}
                    onCellClicked={handleClick}
                    rowSelection={"multiple"}
                    rowMultiSelectWithClick={true}
                />
            </div>
        </>
    );
}

export default Table1;
