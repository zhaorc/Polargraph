/*
 * Drawer.cpp
 *
 *  Created on: 2017年2月12日
 *      Author: richardzhao
 */

#include "Drawer.h"
#include "Common.h"
#include <Arduino.h>
#include <Servo.h>
Drawer::Drawer() {
	pinMode(this->m1_dir_pin, OUTPUT);
	pinMode(this->m1_step_pin, OUTPUT);
	pinMode(this->m2_dir_pin, OUTPUT);
	pinMode(this->m2_step_pin, OUTPUT);

//	segment_length = (long) (segment_length * mm_2_pixel);
//	x1 *= mm_2_pixel;
//	y1 *= mm_2_pixel;
//	pen_offsetX *= mm_2_pixel;
//	pen_offsetY *= mm_2_pixel;
//	diameter *= mm_2_pixel;
//	thread_per_step *= mm_2_pixel;
//
//	distance *= mm_2_pixel;
//	canvas_offsetX *= mm_2_pixel;
//	canvas_offsetY *= mm_2_pixel;

}

void Drawer::initPen() {
	pinMode(relay_pin, OUTPUT);
	digitalWrite(relay_pin, LOW);
	Servo pen = Servo();
	pen.attach(pen_pin);
	this->pen = &pen;
	penUp();
}

void Drawer::draw(DRAW_CMD drawcmd) {
	delay(10);
	switch (drawcmd.cmd) {
		case _CMD_W_:
			setMotor(drawcmd.x);
			break;
		case _CMD_V_:
			setPen(drawcmd.x);
			break;
		case _CMD_U_:
			setPaper(drawcmd.x, drawcmd.y);
			break;
		case _CMD_L_:
			penDown();
			Line(drawcmd.x, drawcmd.y);
			break;
		case _CMD_l_:
			penDown();
			line(drawcmd.x, drawcmd.y);
			break;
		case _CMD_M_:
			penUp();
			Line(drawcmd.x, drawcmd.y);
			break;
		case _CMD_m_:
			penUp();
			line(drawcmd.x, drawcmd.y);
			break;
	}
}
void Drawer::line(float dx, float dy) {
	Line(x1 + dx, y1 + dy);
}

void Drawer::Line(float x2, float y2) {

	float ax1 = x1 + canvas_offsetX - pen_offsetX;
	float ay1 = y1 + canvas_offsetY - pen_offsetY;
	M1A = sqrt(ax1 * ax1 + ay1 * ay1);
	M2A = sqrt((ax1 - distance) * (ax1 - distance) + ay1 * ay1);

	float k = calc_k(x2, y2);
	float b = calc_b(k, x2, y2);
	long dx = abs(x2 - x1);
	long dy = abs(y2 - y1);
	//取dx dy较大者分段计算
	long n = dx >= dy ? dx / segment_length : dy / segment_length;

	long px = x2 > x1 ? segment_length : -segment_length;
	long py = y2 > y1 ? segment_length : -segment_length;
	float pen_x2 = canvas_offsetX + x1;
	float pen_y2 = canvas_offsetY + y1;
	float ax2 = 0, ay2 = 0; //笔的悬挂点的坐标
	float M1B, M2B;
	long steps_m1, steps_m2;
	for (int i = 0; i < n; i++) {
		if (dx > dy) {
			pen_x2 += px;
			pen_y2 = k * pen_x2 + b;
			ax2 = pen_x2 - pen_offsetX;
			ay2 = pen_y2 - pen_offsetY;
		} else {
			pen_y2 += py;
			pen_x2 = dx == 0 ? (canvas_offsetX + x1) : (pen_y2 - b) / k;
			ay2 = pen_y2 - pen_offsetY;
			ax2 = pen_x2 - pen_offsetX;
		}
		//计算马达转动的步数
		M1B = sqrt(ax2 * ax2 + ay2 * ay2);
		M2B = sqrt((distance - ax2) * (distance - ax2) + ay2 * ay2);
		steps_m1 = lround((M1B - M1A) / thread_per_step);
		steps_m2 = lround((M2B - M2A) / thread_per_step);

		step(steps_m1, steps_m2);

		M1A = M1B;
		M2A = M2B;
	}
	long extra = dx >= dy ? dx % segment_length : dy % segment_length;
	if (extra > 0) {
		ax2 = x2 + canvas_offsetX - pen_offsetX;
		ay2 = y2 + canvas_offsetY - pen_offsetY;
		//计算马达转动的步数
		M1B = sqrt(ax2 * ax2 + ay2 * ay2);
		M2B = sqrt((distance - ax2) * (distance - ax2) + ay2 * ay2);
		steps_m1 = lround((M1B - M1A) / thread_per_step);
		steps_m2 = lround((M2B - M2A) / thread_per_step);
		step(steps_m1, steps_m2);
		M1A = M1B;
		M2A = M2B;
	}
	x1 = x2;
	y1 = y2;
}

void Drawer::step(long steps_m1, long steps_m2) {
	digitalWrite(m1_dir_pin, steps_m1 > 0 ? HIGH : LOW);
	digitalWrite(m2_dir_pin, steps_m2 > 0 ? LOW : HIGH);
	unsigned long m1_steps = steps_m1 > 0 ? steps_m1 : -steps_m1;
	unsigned long m2_steps = steps_m2 > 0 ? steps_m2 : -steps_m2;
	unsigned long m1_delay_time, m2_delay_time;
	if (m1_steps > m2_steps) {
		m1_delay_time = step_delay / 2;
		m2_delay_time = lround((float) m1_steps * (float) step_delay / (float) m2_steps / 2);
	} else {
		m2_delay_time = step_delay / 2;
		m1_delay_time = lround((float) m2_steps * (float) step_delay / (float) m1_steps / 2);
	}
	unsigned long now = micros();
	unsigned long m1_time = now, m2_time = now;
	unsigned int m1_state = MOTOR_STATE_PULSE, m2_state = MOTOR_STATE_PULSE;
	while (m1_steps > 0 || m2_steps > 0) {
		//TODO
		now = micros();
		switch (m1_state) {
			case MOTOR_STATE_PULSE:
				if (m1_steps > 0 && now - m1_time > m1_delay_time) {
					m1_time = now;
					digitalWrite(m1_step_pin, HIGH);
					m1_state = MOTOR_STATE_PULSE_DOWN;
				}
				break;
			case MOTOR_STATE_PULSE_DOWN:
				if (now - m1_time > m1_delay_time) {
					m1_time = now;
					digitalWrite(m1_step_pin, LOW);
					m1_state = MOTOR_STATE_PULSE;
					m1_steps--;
				}
				break;
		}
		switch (m2_state) {
			case MOTOR_STATE_PULSE:
				if (m2_steps > 0 && now - m2_time > m2_delay_time) {
					m2_time = now;
					digitalWrite(m2_step_pin, HIGH);
					m2_state = MOTOR_STATE_PULSE_DOWN;
				}
				break;
			case MOTOR_STATE_PULSE_DOWN:
				if (now - m2_time > m2_delay_time) {
					m2_time = now;
					digitalWrite(m2_step_pin, LOW);
					m2_state = MOTOR_STATE_PULSE;
					m2_steps--;
				}
				break;
		}
		delayMicroseconds(10);
	}
}

float Drawer::calc_k(float x2, float y2) {
	return x1 == x2 ? INFINITY : (y2 - y1) / (x2 - x1);
}

float Drawer::calc_b(float k, float x2, float y2) {
	return x1 == x2 ? INFINITY : y2 + canvas_offsetY - k * (x2 + canvas_offsetX);
}

void Drawer::penUp() {
	for (; pen_pos < pen_up_pos; pen_pos++) {
		pen->write(pen_pos);
		delay(pen_move_delay);
	}
}

void Drawer::penDown() {
	for (; pen_pos > pen_down_pos; pen_pos--) {
		pen->write(pen_pos);
		delay(pen_move_delay);
	}
}

void Drawer::setMotor(int distance) {
	this->distance = distance;
}

void Drawer::setPen(int tipWidth) {
	this->mm_2_pixel = (float) 100 / (float) tipWidth;
}

void Drawer::setPaper(int offsetX, int offsetY) {
	this->canvas_offsetX = offsetX;
	this->canvas_offsetY = offsetY;

	segment_length = (long) (segment_length * mm_2_pixel);
	x1 *= mm_2_pixel;
	y1 *= mm_2_pixel;

	pen_offsetX *= mm_2_pixel;
	pen_offsetY *= mm_2_pixel;
	diameter *= mm_2_pixel;
	thread_per_step *= mm_2_pixel;

	distance *= mm_2_pixel;
	canvas_offsetX *= mm_2_pixel;
	canvas_offsetY *= mm_2_pixel;
}
