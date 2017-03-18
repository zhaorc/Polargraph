/*
 * SVGUtil.h
 *
 *  Created on: 2017Äê2ÔÂ26ÈÕ
 *      Author: richardzhao
 */
#ifndef CMDUTIL_H_
#define CMDUTIL_H_

#include "Common.h"

class CMDUtil {
public:
	CMDUtil();
	DRAW_CMD getDrawCmd();

private:
	char buf[MAX_BUF_LENGTH + 1];
	char *buf_ptr;

	void readBuf();
//	int validateCmd();
	char readCmd();
	long readLong();
	void skipWhiteSpace();
	void readToken(char *buf);
//	void requestCmd();
};

#endif /* CMDUTIL_H_ */
