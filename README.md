# Feedback Service

## Table of Contents
* [General Info](#general-information)
* [Documentation](#documentation)
* [Technologies Used](#technologies-used)

## General Information
The Feedback System is a microservice that provides feedback capabilities for various "domain objects" within a larger ecosystem. A domain object is a target entity identified by a domainObjectType (e.g., "POST") and a domainObjectId (a unique ID within that type). The system allows users to express their opinions through reactions, ratings, and comments.
- A Reaction is an interaction like 'LIKE' or 'DISLIKE'.
- A Rating is a numerical score from 1 to 5.
- A Comment is a text message attached to a domain object.

The system serves as a Resource Server, utilizing scopes to manage access permissions for these feedback types.

## Documentation
Complete documentation is available in the [system documentation](https://github.com/dawidbladek0831/f22).

## Technologies Used
- Spring
- Keycloak