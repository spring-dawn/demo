import React from "react";

const toRgba = (hex, opacity) => {
    let value = hex.replace("#", "");

    value.length === 3 &&
        (value =
            value.charAt(0) + value.charAt(0) + value.charAt(1) + value.charAt(1) + value.charAt(2) + value.charAt(2)),
        (value = value.match(/[a-f\d]{2}/gi)),
        (opacity === undefined || opacity > 1) && (opacity = 1);

    return `rgba(${parseInt(value[0], 16)},${parseInt(value[1], 16)},${parseInt(value[2], 16)},${opacity})`;
};

export default { toRgba };
