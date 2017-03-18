/*
 * SVGUtil.cpp
 *
 *  Created on: 2017Äê2ÔÂ26ÈÕ
 *      Author: richardzhao
 */

#include "CMDUtil.h"

#include <Arduino.h>

CMDUtil::CMDUtil() {
	buf_ptr = buf;
}

DRAW_CMD CMDUtil::getDrawCmd() {
	DRAW_CMD drawcmd;
	char c = readCmd();
	if (c == '\0' || c == 10 || c == 13) {
		readBuf();
//		Serial.println("=========================");
//		Serial.println(buf);
		c = readCmd();
		switch (c) {
			case _CMD_M_:
			case _CMD_m_:
			case _CMD_L_:
			case _CMD_l_:
				Serial.println(_SEND_PATH_);
				break;
		}
	}
	drawcmd.cmd = c;
//	Serial.print("cmd=[");
//	Serial.print(drawcmd.cmd);
//	Serial.println("]");
	switch (drawcmd.cmd) {
		case _CMD_W_:
			//distance
			drawcmd.x = readLong();
			break;
		case _CMD_V_:
			drawcmd.x = readLong();
			break;
		case _CMD_U_:
			drawcmd.x = readLong();
			drawcmd.y = readLong();
			break;
		case _CMD_X_:
			Serial.println(_SEND_PATH_);
			break;
		case _CMD_M_:
		case _CMD_m_:
		case _CMD_L_:
		case _CMD_l_:
			drawcmd.x = readLong();
			drawcmd.y = readLong();
			break;
		default:
			drawcmd.cmd = _CMD_NONE_;
	}
//	Serial.print("[");
//	Serial.print(drawcmd.cmd);
//	Serial.print(" ");
//	Serial.print(drawcmd.x);
//	Serial.print(" ");
//	Serial.print(drawcmd.y);
//	Serial.println("]");
//	Serial.println("-----------------------------");

	return drawcmd;
}

void CMDUtil::readBuf() {
//	int read_length = 0;
//	while (true) {
//		if (!Serial.available()) {
//			continue;
//		}
//		read_length = Serial.readBytes(buf, MAX_BUF_LENGTH);
//		buf[read_length] = '\0';
//		buf_ptr = buf;
//		break;
//	}
	int read_length = 0;
	int data = 0;
	while (true) {
		if (!Serial.available()) {
			continue;
		}
		data = Serial.read();
		if (data == '\n') {
			buf[read_length] = '\0';
			buf_ptr = buf;
			break;
		} else {
			buf[read_length++] = data;
		}
	}
}

char CMDUtil::readCmd() {
	skipWhiteSpace();
	return *buf_ptr++;
}

long CMDUtil::readLong() {
	char buf[20];
	skipWhiteSpace();
	readToken(buf);
	return atol(buf);
}

void CMDUtil::skipWhiteSpace() {
	char c;
	while (true) {
		c = *buf_ptr++;
		if (c != ' ' && c != 13 && c != 10) {
			buf_ptr--;
			break;
		}
	}
}

void CMDUtil::readToken(char *buf) {
	char c;
	int read_length = 0;
	while (true) {
		c = *buf_ptr++;
		if ((c >= '0' && c <= '9') || c == '-') {
			*(buf + read_length++) = c;
		} else {
			buf_ptr--;
			break;
		}
	}
	*(buf + read_length) = '\0';
}
