import jwt from 'jsonwebtoken';
import { promisify } from 'util';

import AuthException from './AuthException.js';

import { API_SECRET } from '../constants/secrets.js';
import {
  INTERNAL_SERVER_ERROR,
  UNAUTHORIZED,
} from '../constants/httpStatus.js';

const emptySpace = ' ';

export default async (req, res, next) => {
  try {
    const { authorization } = req.headers;

    if (!authorization) {
      throw new AuthException(UNAUTHORIZED, 'Access token was not informed.');
    }

    let accessToken = authorization;
    if (accessToken.includes(emptySpace)) {
      accessToken = accessToken.split(emptySpace)[1];
    }

    const decoded = await promisify(jwt.verify)(accessToken, API_SECRET);

    req.authUser = decoded.authUser;

    return next();
  } catch (error) {
    const status = error.status ? error.status : INTERNAL_SERVER_ERROR;
    return res.status(status).json({
      status,
      message: error.message,
    });
  }
};
