# CopyVMWareVM
원본 VM 으로부터 복사 후 파일명들을 바꾸고 내용을 알맞게 바꿉니다.

# Usage
git clone https://github.com/HaNeul-Kim/CopyVMWareVM  
cd CopyVMWareVM  
mvn clean install -DskipTests  
java -jar target/CopyVMWare-0.1-SNAPSHOT.jar --sourcePath /path/to/source/vm --sourceVMName centos_6.7_template --targetPath /path/to/target/vm --targetVMNames test1,test2 --encoding UTF-8
