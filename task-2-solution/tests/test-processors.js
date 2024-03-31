"use strict";

module.exports = {
  processResponse: function (requestParams, response, context, ee, next) {
    try {
      const responseBody = JSON.parse(response.body);

      context.vars._id = responseBody.id;

      return next();
    } catch (error) {
      console.error("Error processing response:", error);
      return next(error);
    }
  },
  compareResponse: function (requestParams, response, context, ee, next) {
    try {
      const responseBody = JSON.parse(response.body);
      if (!responseBody.data) {
        console.warn(
          `Inconsistency detected! Made request for ${context.vars._id} and received "${responseBody.message}"`,
        );
      }

      return next();
    } catch (error) {
      console.error("Error comparing responses:", error);
      return next(error);
    }
  },
};
