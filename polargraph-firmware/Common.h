/*
 * Common.h
 *
 *  Created on: 2017Äê2ÔÂ26ÈÕ
 *      Author: richardzhao
 */

#ifndef COMMON_H_
#define COMMON_H_

#include <Arduino.h>

#define MOTOR_STATE_PULSE 1
#define MOTOR_STATE_PULSE_DOWN 2

#define _CMD_NONE_ ('0')
//controller is ready
#define _CMD_X_ ('X')
#define _CMD_W_ ('W')
#define _CMD_V_ ('V')
#define _CMD_U_ ('U')
//gcode
#define _CMD_L_ ('L')
#define _CMD_l_ ('l')
#define _CMD_M_ ('M')
#define _CMD_m_ ('m')
//give me path
#define _SEND_PATH_ ("_SEND_PATH_")

#define MAX_BUF_LENGTH  (64)

struct DRAW_CMD {
	char cmd;
	long x;
	long y;
};

const unsigned long speed = 10;
const unsigned long micro_steps = 32;
const unsigned long steps = 200;
const unsigned long step_delay = lround((long) 1000 * (long) 60000 / speed / steps / micro_steps);
const unsigned int pen_up_pos = 110;
const unsigned int pen_down_pos = 90;
const unsigned int pulse_down_time = 10;
const unsigned int pen_move_delay = 35;
const unsigned int segment = 5;
const float motor_diameter = 15.66;

#endif /* COMMON_H_ */
