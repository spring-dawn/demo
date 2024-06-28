import React, { useState } from "react";

let alertMessage = {
    // login
    login_failed: {
        title: "Error!",
        text: "아이디와 비밀번호를 확인해주세요",
        icon: "error",
    },
    login_success: {
        title: "Success!",
        text: "로그인 되었습니다.",
        icon: "success",
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        showConfirmButton: false,
        showCancelButton: false,
        timer: 1000,
    },
    // find pw
    req_diff: {
        title: "Error!",
        text: "입력하신 정보와 일치하는 내용이 없습니다.",
        icon: "error",
    },

    // user insert
    userId_exist_true: {
        title: "Success!",
        text: "사용 가능한 아이디입니다.",
        icon: "success",
    },
    userId_exist_false: {
        title: "Error!",
        text: "사용 중인 아이디입니다.",
        icon: "error",
    },
    password_diff: {
        title: "Error!",
        text: "비밀번호가 일치하지 않습니다.",
        icon: "error",
    },
    insert_success: {
        title: "Success!",
        text: "등록되었습니다.",
        icon: "success",
    },
    isRegister: {
        text: "등록하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "등록",
        cancelButtonText: "취소",
        reverseButtons: true,
    },

    // update
    update_success: {
        title: "Success!",
        text: "업데이트 완료되었습니다.",
        icon: "success",
    },
    findPw: {
        html: "등록하신 이메일로 임시 비밀번호를 전송합니다. <br>계속하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "발급",
        cancelButtonText: "취소",
        reverseButtons: true,
    },

    findPwMailing: {
        title: "Success!",
        text: "임시 비밀번호 발급 메일이 전송되었습니다.",
        icon: "success",
    },

    isUpdate: {
        text: "수정하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "수정",
        cancelButtonText: "취소",
        reverseButtons: true,
    },

    // delete
    delete: {
        title: "Success!",
        text: "삭제되었습니다.",
        icon: "success",
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        showConfirmButton: false,
        showCancelButton: false,
        timer: 1000,
    },

    delete_fail: {
        title: "Error!",
        text: "이미 사용 중이거나 삭제할 수 없는 대상입니다.",
        icon: "error",
    },

    double_check: {
        // title: "Are you sure?",
        text: "삭제하면 복구할 수 없습니다. 삭제하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "삭제",
        cancelButtonText: "취소",
        reverseButtons: true,
    },
    data_absolute_dup: {
        title: "Error!",
        text: "완전중복 파일은 데이터를 승인할 수 없습니다.",
        icon: "error",
    },
    data_partial_dup: {
        title: "Error!",
        text: "부분중복 데이터 승인은 관리자 권한이 필요합니다.",
        icon: "error",
    },
    double_check_collect: {
        // title: "Are you sure?",
        html: "해당 파일의 데이터를 수집합니다. <br>수집하는 동안 다른 작업을 하실 수 있습니다. <br>진행하시겠습니까?",
        icon: "info",
        confirmButtonText: "수집",
        showCancelButton: true,
        cancelButtonText: "취소",
        showDenyButton: true,
        denyButtonText: "반려",
        // reverseButtons: true,
    },
    update_status: {
        html: "해당 건의 처리상태를 '완료'로 변경합니다. <br>진행하시겠습니까?",
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "처리완료",
        cancelButtonText: "취소",
        reverseButtons: true,
    },
};

let inputValid = {
    cellNo: {
        value: /^\d{3}-\d{3,4}-\d{4}$/,
        message: "'000-000-0000' 형식으로 입력해주세요",
    },
    email: {
        value: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
        message: "이메일 형식이 아닙니다.",
    },
    password: {
        value: /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\-_=+\\\|\[\]{};:\'",.<>/?]).{9,20}$/,
        message: "비밀번호 형식이 아닙니다.",
    },
};

export default { alertMessage, inputValid };
