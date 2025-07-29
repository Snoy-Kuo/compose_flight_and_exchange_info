import json
import os
import sys

# 欲保留的欄位
FIELDS_TO_KEEP = [
    "AirlineID",
    "Terminal",
    "FlightNumber",
    "Gate",
    "ScheduleArrivalTime",
    "ScheduleDepartureTime",
    "EstimatedArrivalTime",
    "EstimatedDepartureTime",
    "ArrivalAirportID",
    "DepartureAirportID",
    "ArrivalRemark",
    "DepartureRemark",
    "AcType"
]

def simplify_flight_data(input_filename):
    if not os.path.isfile(input_filename):
        print(f"❌ 檔案不存在: {input_filename}")
        return

    # 讀取原始 JSON
    with open(input_filename, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # 建立新的清單，只保留有值的欄位
    simplified = []
    for entry in data:
        item = {
            key: value
            for key, value in entry.items()
            if key in FIELDS_TO_KEEP and value is not None
        }
        simplified.append(item)

    # 輸出檔名
    base_name = os.path.splitext(os.path.basename(input_filename))[0]
    output_filename = f"{base_name}_simplified.json"

    # 儲存 prettified JSON
    with open(output_filename, 'w', encoding='utf-8') as f:
        json.dump(simplified, f, ensure_ascii=False, indent=4)

    print(f"✅ 簡化結果已儲存為: {output_filename}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python simplify_flights.py <輸入檔案.json>")
    else:
        simplify_flight_data(sys.argv[1])
