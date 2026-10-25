# Rush Hour — Java command-line game

Trò chơi Rush Hour hai bên: người chơi điều khiển mục tiêu của hero **A**,
máy có hero **B**. Mỗi bên cố gắng đưa hero của mình ra ngoài qua cửa riêng.
Giao diện và thông báo trong chương trình sử dụng tiếng Anh.

## Yêu cầu và cách chạy

- JDK 21.
- Maven, hoặc Maven tích hợp trong IntelliJ IDEA.
- Internet khi tải dependency lần đầu.

### Chạy bằng IntelliJ IDEA

1. Mở project chứa `pom.xml`, chờ IntelliJ tải các dependency Maven.
2. Chọn Project SDK là Java 21.
3. Mở `src/main/java/org/example/Main.java` và chạy `Main.main()`.
4. Nhập lệnh trong cửa sổ Run, nhấn Enter để gửi lệnh.

Không cần cài Maven vào PATH để chạy qua IntelliJ. Các Maven goal có thể chạy
trong cửa sổ **View → Tool Windows → Maven → Execute Maven Goal**.

### Chạy bằng terminal

Tại thư mục chứa `pom.xml`:

```sh
mvn clean install
java -jar target/Rushhour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Nếu terminal báo không nhận diện `mvn`, dùng cửa sổ Maven của IntelliJ hoặc
cài Maven và thêm thư mục `bin` của Maven vào PATH.

## Luật chơi

- Người chơi đi trước. Sau mỗi bước hợp lệ, máy đi đúng một lượt.
- Trong lượt của mình, mỗi bên được di chuyển **bất kỳ xe nào**, kể cả hero đối thủ.
- Xe chỉ trượt theo hướng cố định: ngang hoặc dọc; không được xoay.
- Một bước có thể đi nhiều ô, nhưng toàn bộ đường đi phải trống.
- Xe không được chồng lên nhau hoặc nhảy qua xe khác.
- Xe thường không được ra khỏi bàn cờ.
- Hero chỉ được ra ngoài qua cửa riêng, đúng hàng và đúng phía.
- Trong bản hiện tại, một bước thoát phải đưa **toàn bộ hero** ra ngoài;
  bước khiến xe nằm nửa trong, nửa ngoài bị từ chối.
- A thoát thành công thì người chơi thắng; B thoát thành công thì máy thắng,
  bất kể bên nào vừa di chuyển xe đó. Trò chơi kết thúc ngay khi có hero thoát.
- Lệnh sai, bước không hợp lệ, `show` và `help` không làm mất lượt.
- Nếu không còn bước hợp lệ cho bất kỳ xe nào, chương trình kết thúc với kết quả hòa.

## Bàn cờ mặc định

```text
. . . . C .
. . . . C .
. A A . C .
. D . . . .
. D B B . .
. D . . . .
```

Hàng và cột trong model bắt đầu từ **0**. Dấu `.` là ô trống.

| Xe | Vai trò | Hướng | Độ dài | Vị trí đầu (hàng, cột) |
| --- | --- | --- | --- | --- |
| A | Hero người chơi | Ngang | 2 | (2, 1) |
| B | Hero máy | Ngang | 2 | (4, 2) |
| C | Xe chắn | Dọc | 3 | (0, 4) |
| D | Xe chắn | Dọc | 3 | (3, 1) |

A có cửa bên phải hàng 2; B có cửa bên trái hàng 4.
C chắn đường A, còn D chắn đường B. Cấu hình nằm trong `Main.createBoard()`.
`Board` hỗ trợ kích thước hàng/cột dương khác; bản chạy mặc định dùng 6 × 6.

## Các lệnh

| Lệnh | Tác dụng |
| --- | --- |
| `show` | In bàn cờ hiện tại và thông tin cửa thoát |
| `help` | Hiện hướng dẫn |
| `move A 1` | Di chuyển A sang phải 1 ô |
| `move B -1` | Di chuyển B sang trái 1 ô |
| `move C 3` | Di chuyển C xuống 3 ô |
| `move D -1` | Di chuyển D lên 1 ô, nếu đường trống |
| `quit` | Thoát chương trình |

Số bước dương: phải/xuống. Số bước âm: trái/lên. Bước 0 không hợp lệ.
Tên lệnh không phân biệt hoa thường, nhưng ID xe có phân biệt (`A` khác `a`).
Ví dụ các lệnh trên minh họa cú pháp, không bảo đảm đều hợp lệ tại mọi trạng thái.

## AI ngẫu nhiên

`ComputerPlayer` thực hiện ba bước:

1. Tìm các xe có ít nhất một bước hợp lệ.
2. Chọn ngẫu nhiên một xe trong danh sách đó.
3. Chọn ngẫu nhiên một bước hợp lệ của xe đã chọn.

AI dùng `java.util.Random`, không có tìm kiếm chiến thuật hoặc ưu tiên thắng.
AI có thể vô tình giúp A thoát. Kết quả mỗi ván có thể khác nhau.
`Board.canMoveVehicle()` chỉ kiểm tra, không làm thay đổi bàn cờ khi AI chọn bước.

## Cấu trúc code

| Lớp | Trách nhiệm |
| --- | --- |
| `Main` | Tạo bàn cờ mẫu và gọi `Game.start()` |
| `Game` | Đọc lệnh, điều phối lượt người–máy, thông báo kết quả |
| `Board` | Quản lý xe, kiểm tra bước đi, thực hiện di chuyển và xác định hero thắng |
| `Vehicle` | Lưu ID, độ dài, hướng, vị trí và cờ hero |
| `Position` | Value object tọa độ bất biến, có `equals`, `hashCode`, `toString` |
| `Orientation` | Hai hướng xe: ngang và dọc |
| `Exit` | Lưu hero được phép thoát, hàng cửa và phía trái/phải |
| `Move` | Lưu xe và số bước của một lựa chọn di chuyển |
| `ComputerPlayer` | Chọn bước hợp lệ ngẫu nhiên |
| `BoardPrinter` | In bàn cờ và thông tin cửa thoát |

Luồng một lượt: `Game` đọc lệnh → `Board` kiểm tra và di chuyển →
`BoardPrinter` hiển thị → nếu chưa thắng, `ComputerPlayer` chọn bước cho máy.

## Kiểm thử và công cụ Maven

Chạy trong terminal, hoặc nhập phần goal (không có `mvn`) vào Maven của IntelliJ:

```sh
mvn test
mvn clean verify
mvn checkstyle:check
```

- `test`: chạy các test JUnit 5.
- `verify`: chạy test, đóng gói JAR và tạo báo cáo JaCoCo.
- `checkstyle:check`: kiểm tra định dạng và quy ước code riêng biệt.

Mở `target/site/jacoco/index.html` bằng trình duyệt để xem coverage.
Trên Windows PowerShell có thể dùng:

```powershell
Start-Process .\target\site\jacoco\index.html
```

Các test kiểm tra model, đường đi, cửa thoát, thắng/thua, xử lý lệnh, chọn bước
ngẫu nhiên và luân phiên lượt. Test tương tác dùng `Scanner` với chuỗi lệnh rồi
kiểm tra trạng thái bàn cờ; không tự so sánh nội dung được in ra console.
Test AI dùng seed cố định hoặc đối tượng máy có bước đi định trước để kiểm tra
có thể lặp lại.

Cấu hình hiện tại tạo báo cáo coverage nhưng **chưa bắt buộc ngưỡng 80%**.
Checkstyle chưa được gắn tự động vào lifecycle build; cần chạy goal riêng.
`BUILD SUCCESS` không đồng nghĩa không có cảnh báo hoặc code không còn lỗi.

## Phạm vi hiện tại và các ý cần trình bày

Bản hiện tại có model, CLI, lượt người–máy, AI ngẫu nhiên, cửa thoát trái/phải và
điều kiện kết thúc. Có thể dùng để demo quy trình chơi đến khi có hero thắng.

Chưa có đọc/ghi bàn cờ từ file, database/high score, lưu ván đang chơi hoặc cửa
trên/dưới. Bàn cờ khởi tạo được viết trực tiếp trong code. Đây chưa phải toàn bộ
các chức năng được liệt kê trong yêu cầu chung cuối kỳ.

Khi bảo vệ, nên giải thích được:

1. Vì sao tách model, điều phối game và phần in bàn cờ.
2. `canMoveVehicle()` khác `moveVehicle()` như thế nào.
3. Cách kiểm tra mọi ô trên đường đi, thay vì chỉ kiểm tra vị trí đích.
4. Vì sao chỉ hero đúng cửa mới được vượt biên.
5. Vì sao `Position` bất biến và cần cả `equals()` lẫn `hashCode()`.
6. Cách chọn xe rồi chọn bước ngẫu nhiên; vì sao bỏ qua xe bị kẹt.
7. Vì sao lệnh sai không đổi lượt và tại sao phải dừng ngay sau bước thắng.

Để demo, có thể thử `show`, một lệnh sai như `move A abc`, bước bị chắn ban đầu
`move A 5`, rồi bước mở đường `move C 3`. Sau đó chọn bước dựa trên bàn cờ mới,
vì AI tự di chuyển giữa các lượt; không có chuỗi lệnh cố định bảo đảm thắng.
