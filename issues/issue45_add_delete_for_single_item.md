 Add `DELETE` endpoint for a specific record in Book table.

# Acceptance Criteria:

- [ ] In `BookController.java` there is code for an 
      endpoint `DELETE /api/Book?id=123` endpoint 
      that deletes the record if it exists, and returns 200 (ok) and 
      the text `record 123 deleted`, or returns 404 (Not Found) and
      the text `record 123 not found` if it does not.
- [ ] The Swagger-UI endpoints for this endpoint is well documented
      so that any member of the team can understand how to use it.
- [ ] The endpoint works as expected on localhost.
- [ ] The endpoint works as expected when deployed to Dokku.
- [ ] There is full test coverage (Jacoco) for the new code in 
      `BookController.java`
- [ ] There is full mutation test coverage (Pitest) for new code in
      `BookController.java`


