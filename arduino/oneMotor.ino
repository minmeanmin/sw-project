#include <Stepper.h>
#include <SoftwareSerial.h>

SoftwareSerial s(2, 3); // 2:RX, 3:TX
int irPin = 5;
int stepsPerRev = 2048;
int value;

Stepper stepper(stepsPerRev, 11, 9, 10, 8);

void setup() {
  Serial.begin(9600);
  s.begin(9600);
  stepper.setSpeed(20);
  pinMode(irPin, INPUT);
}

void loop() {
  value = digitalRead(irPin);
  if (value == HIGH) {
    delay(1000); // 신호 대기
  } else {
    stepper.step(9000); // Stepper 동작
    s.write('a');        // 'a' 전송
    Serial.println("Signal 'a' sent!"); // Debug message
    delay(1000); // 송신 후 대기 (신호 간섭 방지)
  }
}
