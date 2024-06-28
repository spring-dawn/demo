import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Select as AntSelect, Space, Dropdown, Menu, Checkbox } from "antd";

// GIS 상단 유틸 셀렉트 컴포넌트 (체크박스)
function GisCheckBoxUtile({ value, set, options, label }) {
    const [visible, setVisible] = useState(false);

    const createList = (options, value) => {
        return (
            <Menu>
                {options.map((ele) => {
                    return (
                        <Menu.Item key={ele.value}>
                            <Checkbox
                                onChange={(e) => {
                                    setVisible(true);

                                    const checked = e.target.checked;

                                    if (checked) {
                                        set([...value, ele.value]);
                                    } else {
                                        set(value.filter((check) => check != ele.value));
                                    }
                                }}
                                checked={value.find((check) => check == ele.value) ? true : false}
                            >
                                {ele.label}
                            </Checkbox>
                        </Menu.Item>
                    );
                })}
            </Menu>
        );
    };

    const handleVisibleChange = (flag) => {
        setVisible(flag);
    };

    return (
        <ul className="list bg">
            <div className="title">{label}</div>
            <Dropdown overlay={createList(options, value)} onVisibleChange={handleVisibleChange} visible={visible}>
                <a onClick={(e) => e.preventDefault()}>
                    <Space
                        style={{
                            width: 100,
                            height: 32,
                            position: "relative",
                            backgroundColor: "#fff",
                            border: "1px solid #d9d9d9",
                            borderRadius: "2px",
                            transition: "all .3s cubic-bezier(.645,.045,.355,1)",
                            padding: "0 11px",
                            fontWeight: "500",
                        }}
                        wrap
                    >
                        선택
                    </Space>
                </a>
            </Dropdown>
        </ul>
    );
}

export default GisCheckBoxUtile;
