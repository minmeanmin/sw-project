#include <Stepper.h>
#include <SoftwareSerial.h>

SoftwareSerial s(2, 3); // 2:RX, 3:TX
char data = 0; // 수신 데이터 저장
int stepsPerRev = 2048;

Stepper stepper(stepsPerRev, 11, 9, 10, 8);
Stepper stepper2(stepsPerRev, 7, 5, 6, 4);

void setup() {
  Serial.begin(9600);
  s.begin(9600);
  stepper.setSpeed(17);
  stepper2.setSpeed(17);
  pinMode(13, OUTPUT); // LED 핀 설정
}

void loop() {
  // SoftwareSerial로 데이터 수신
  if (s.available()) {
    data = s.read(); // 데이터 읽기
    Serial.print("Received data: "); 
    Serial.println(data); // 디버그 출력
  }

  // data 값이 어떤 데이터든 수신되면 조건 실행
  if (data != 0) {
    digitalWrite(13, HIGH); // LED ON
    stepper.step(4000);     // 첫 번째 스테퍼 동작
    stepper2.step(10000);   // 두 번째 스테퍼 동작
    delay(1000);            // 간격 대기

    // 초기화
    digitalWrite(13, LOW); 
    data = 0; // data 초기화
  }
}
