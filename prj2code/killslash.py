import sys

def main():
	inputfile = open(sys.argv[1],'r')
	outputfile = open(sys.argv[2],'w')
	
	for line in inputfile:
		key = ''
		data = ''
		switch = False
		for char in line:
			if char == '\\':
				if switch == False:
					key = key + '&92'
				else:
					data = data + '&92'
			elif char == ':':
				switch = True
			else:
				if switch == False:
					key = key + char
				else:
					data = data + char

		outputfile.write(key + '\n')
		outputfile.write(data)

	inputfile.close()
	outputfile.close()

if __name__ == "__main__":
    main()
