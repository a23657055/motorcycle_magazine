# springboot-template

## IDE
Spring Tool Suite 4

## 在eclipse IDE安裝Lombok，以便Lombok程式碼可以正常顯示(Mac版指令)
   * 1. 確認IDE在停止狀態，請關閉正在運行的IDE
   * 2. 使用java -jar指令執行Lombook jar檔，例如：java  -jar  ~/.m2/repository/org/projectlombok/lombok/1.18.8/lombok-1.18.8.jar
   * 3. 安裝程式介面圖示請參考：https://projectlombok.org/setup/eclipse
   注意：安裝程式通常無法正確找到eclipse IDE(尤其是在同一台機器上安裝了多個IDE的情況下)
   * 4. 使用Specify Location按鈕手動尋找eclipse IDE的安裝位置，對Spring Tool Suite 4來說，就是SpringToolSuite4.ini檔的位置，例如：<STS4安裝目錄>/SpringToolSuite4.app/Contents/Eclipse/SpringToolSuite4.ini
   * 5. 找到檔案後使用Install/Update按鈕安裝，使用Quite Installer退出安裝程式，使用eclipse IDE的about畫面檢查Lombok安裝成功
   
參考https://blog.csdn.net/WYpersist/article/details/85012174   
https://stackoverflow.com/questions/52780535/lombok-with-spring-tool-suite-4
