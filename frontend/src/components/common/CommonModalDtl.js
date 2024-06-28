import React, { useEffect, useState } from "react";

import { AgGridReact } from "ag-grid-react";

function CommonModalDtl(props) {
    const { data } = props;

    return (
        <div className="dtlTableWrap">
            <div className="ag-theme-alpine" style={{ width: "100%", height: "300px" }}>
                <AgGridReact
                    rowData={data.data}
                    columnDefs={data.columnDefs}
                    // defaultColDef={defaultColDef}
                    rowSelection="multiple"
                    // onGridReady={onGridReady}
                    // onCellClicked={cellClickedListener}
                    // pagination={true}
                    // paginationPageSize={10}
                />
            </div>
        </div>
    );
}

export default CommonModalDtl;
