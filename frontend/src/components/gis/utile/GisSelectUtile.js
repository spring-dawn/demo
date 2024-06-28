import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Select as AntSelect, Space, Dropdown } from "antd";

// GIS 상단 유틸 셀렉트 컴포넌트
function GisSelectUtile({ value, set, options, label }) {
    return (
        <ul className="list bg">
            <div className="title">{label}</div>
            <Space wrap>
                <AntSelect style={{ width: 100 }} value={value} options={options} onChange={(value) => set(value)} />
            </Space>
        </ul>
    );
}

export default GisSelectUtile;
