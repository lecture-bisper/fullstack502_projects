function DatePicker({setStartDate, setEndDate, title, width = "auto", value = {startDate: "", endDate: ""}}) {
    return (
        <div
            style={{display: "flex", alignItems: "center", gap: "8px", width}}
            className={'date-range-picker input'}
        >
            <div style={{fontSize: "18px"}} className={"selectBoxText"}>{title}</div>
            <input
                className={"datePickerInput"}
                type="date"
                value={value.startDate}
                onChange={(e) => setStartDate(e.target.value)}
                style={{flex: 1}}
            />
            {" ~ "}
            <input
                className={"datePickerInput"}
                type="date"
                value={value.endDate}
                onChange={(e) => setEndDate(e.target.value)}
                style={{flex: 1}}
            />
        </div>
    );
}

export default DatePicker;
