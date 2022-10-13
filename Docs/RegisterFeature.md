this is how the register new user works. 
register only asks for an Email, password and repeat password, no username or phone-nr needed. 
once the user provides all three and they are valid (right email format, password has to have at least 8 chars with one 
being a capital letter, one symbol, numbers and letters) we send a post request to the backend to store the data. 
once that is successful, we send a conformation code to the users email and in the app we go into the verify activity, where
the user gives his email and conformation code. 
we send a post request and if its successful then we go into the Login page and we let the user login. 
