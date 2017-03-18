#include <Arduino.h>

#include "CMDUtil.h"
#include "Drawer.h"
#include "Common.h"

#define LED_PIN 13

Drawer drawer;
CMDUtil cmdUtil;

void setup() {
	delay(3000);
	pinMode(LED_PIN, OUTPUT);
	digitalWrite(LED_PIN, HIGH);
	Serial.begin(115200);
	drawer.initPen();
	delay(1000);
	Serial.println("i am ready.");

//	DRAW_CMD drawcmd;
//	drawcmd.cmd = _CMD_m_;
//	drawcmd.x = 0;
//	drawcmd.y = -1000;
//	drawer.draw(drawcmd);

}

void loop() {
	DRAW_CMD drawcmd = cmdUtil.getDrawCmd();
	drawer.draw(drawcmd);
}

//void test0() {
//	drawer.draw(_CMD_l_, 100, 0);
//}
//
//void test1() {
//	int draw_length = 100;
//	for (int i = 0; i < 10; i++) {
//		drawer.draw(_CMD_l_, draw_length, 0);
//		drawer.draw(_CMD_l_, 0, draw_length);
//		draw_length--;
//		drawer.draw(_CMD_l_, -draw_length, 0);
//		drawer.draw(_CMD_l_, 0, -draw_length);
//		draw_length--;
//	}
//	drawer.penUp();
//}
//
//void test2() {
//	int draw_length = 0;
//	int offset = 1;
////	drawer.draw(_CMD_M_, 40, 170);
//	for (int i = 0; i < 100; i++) {
//		draw_length += offset;
//		drawer.draw(_CMD_l_, draw_length, -draw_length);
//		drawer.draw(_CMD_l_, offset, 0);
//		draw_length += offset;
//		drawer.draw(_CMD_l_, -draw_length, draw_length);
//		drawer.draw(_CMD_l_, 0, offset);
//	}
//	drawer.penUp();
//}
//
//void test3() {
//	int draw_length = 100;
//	int offset = 10;
//	drawer.draw(_CMD_l_, draw_length, 0);
//	drawer.draw(_CMD_l_, 0, draw_length);
//	drawer.draw(_CMD_l_, -draw_length, 0);
//	drawer.draw(_CMD_l_, 0, -draw_length);
//	for (int i = 0; i < 4; i++) {
//		drawer.draw(_CMD_m_, 0, offset);
//		drawer.draw(_CMD_l_, draw_length, 0);
//		drawer.draw(_CMD_m_, 0, offset);
//		drawer.draw(_CMD_l_, -draw_length, 0);
//	}
//	drawer.draw(_CMD_m_, 0, offset);
//	for (int i = 0; i < 4; i++) {
//		drawer.draw(_CMD_m_, offset, 0);
//		drawer.draw(_CMD_l_, 0, -draw_length);
//		drawer.draw(_CMD_m_, offset, 0);
//		drawer.draw(_CMD_l_, 0, draw_length);
//	}
//	drawer.draw(_CMD_M_, 20, 20);
//	drawer.penUp();
//}
//
//void test4() {
//	int draw_length = 100;
//	int offset = 10;
//	int dir = 1;
//	drawer.draw(_CMD_M_, 40, 160);
//	for (int i = 0; i < 11; i++) {
//		dir = i % 2 == 0 ? 1 : -1;
//		drawer.draw(_CMD_m_, 0, offset);
//		drawer.draw(_CMD_l_, dir * draw_length, 0);
//	}
//	for (int i = 0; i < 11; i++) {
//		dir = i % 2 == 0 ? -1 : 1;
//		drawer.draw(_CMD_l_, 0, dir * draw_length);
//		drawer.draw(_CMD_m_, offset, 0);
//	}
//	drawer.draw(_CMD_M_, 20, 20);
//	drawer.penUp();
//}
