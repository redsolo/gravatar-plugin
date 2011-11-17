import os, sys

def createUser(username, basedir):
    userpath = basedir + os.sep + username
    try:
        os.makedirs(userpath)
        file = open(userpath+os.sep+"config.xml","w")
        # Write all the lines at once:
        file.write("<?xml version='1.0' encoding='UTF-8'?><user><fullName>%s</fullName><properties><hudson.tasks.Mailer_-UserProperty><emailAddress>%s@example.com</emailAddress></hudson.tasks.Mailer_-UserProperty></properties></user>" % (username, username))
        file.close()
    except OSError:
        pass
    
def generateUsers(count, usersPath):
    for i in range(0,count):
        createUser("user-%i" % (i), usersPath)
    
def main(argv=None):
    if argv is None:
        argv = sys.argv

    if len(argv) != 3:
        print "Usage users_base_path count"
        return 1

    generateUsers(int(argv[2]),argv[1])

    return 0
    
if __name__ == "__main__":
    sys.exit(main())
