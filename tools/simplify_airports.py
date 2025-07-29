import json
import os
import sys

def simplify_airports(input_filename):
    # 確認檔案存在
    if not os.path.isfile(input_filename):
        print(f"檔案不存在: {input_filename}")
        return

    # 讀取原始資料
    with open(input_filename, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # 簡化每筆資料
    simplified = [
        {
            "AirportID": entry.get("AirportID"),
            "AirportName": entry.get("AirportName")
        }
        for entry in data
    ]

    # 構造輸出檔名
    base_name = os.path.splitext(os.path.basename(input_filename))[0]
    output_filename = f"{base_name}_simplified.json"

    # 儲存 prettified JSON
    with open(output_filename, 'w', encoding='utf-8') as f:
        json.dump(simplified, f, ensure_ascii=False, indent=4)

    print(f"已儲存為：{output_filename}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法：python simplify_airports.py <輸入檔案.json>")
    else:
        simplify_airports(sys.argv[1])
