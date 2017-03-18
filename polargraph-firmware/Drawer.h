/*
 * Drawer.h
 *
 *  Created on: 2017Äê2ÔÂ12ÈÕ
 *      Author: richardzhao
 */

#ifndef DRAWER_H_
#define DRAWER_H_

#include <Arduino.h>
#include <Servo.h>
#include "Common.h"

class Drawer {
public:
	Drawer();
	void initPen();
	void draw(DRAW_CMD drawcmd);
	void penUp();
	void penDown();

private:
	const unsigned int m1_step_pin = 6;
	const unsigned int m1_dir_pin = 7;
//	const unsigned int m1_lock_pin = 8;
	const unsigned int m2_step_pin = 9;
	const unsigned int m2_dir_pin = 10;
//	const unsigned int m2_lock_pin = 11;
	const unsigned int pen_pin = 4;
	const unsigned int relay_pin = 12;

	long segment_length = segment;
	float mm_2_pixel = 4;
	float distance = 450;
	float canvas_offsetX = 118;
	float canvas_offsetY = 255;
	float pen_offsetX = 0;
	float pen_offsetY = 0;
	float diameter = motor_diameter;
	float thread_per_step = 3.14159 * motor_diameter / steps / micro_steps;
	float x1 = 60;
	float y1 = 55;
	float M1A = 0;
	float M2A = 0;

	Servo *pen;
	int pen_pos = 90;

	void step(long left_steps, long right_steps);
	void line(float dx, float dy);
	void Line(float x2, float y2);
	float calc_k(float x2, float y2);
	float calc_b(float k, float x2, float y2);
	void setMotor(int distance);
	void setPen(int tipWidth);
	void setPaper(int offsetX, int offsetY);
};

#endif /* DRAWER_H_ */
