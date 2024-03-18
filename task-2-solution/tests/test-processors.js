"use strict";

module.exports = {
  processResponse: function (requestParams, response, context, ee, next) {
    try {
      const responseBody = JSON.parse(response.body);

      context.vars._id = responseBody._id;

      return next();
    } catch (error) {
      console.error("Error processing response:", error);
      return next(error);
    }
  },
  compareResponse: function (requestParams, response, context, ee, next) {
    try {
      if (!response.body) {
        console.warn(
          `Inconsistency detected! Made request for ${context.vars._id} and received "${response.body}"`,
        );
      }

      return next();
    } catch (error) {
      console.error("Error comparing responses:", error);
      return next(error);
    }
  },
};
