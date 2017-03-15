#!/usr/bin/python

import sys, getopt
import matplotlib.pyplot as plt


def main(argv):
	inputfile = ''
	outputfile = ''
	try: 
            opts, args = getopt.getopt(argv,"hi:o:",["help","ifile=","ofile="]) 
	except getopt.GetoptError: 
		print 'test.py -i <inputfile> -o <outputfile>' 
		sys.exit(2)
	for opt, arg in opts:
		if opt in ("-h", "--help"): 
			print 'test.py -i <inputfile> -o <outputfile>' 
			sys.exit() 
		elif opt in ("-i", "--ifile"): 
			inputfile = arg 
		elif opt in ("-o", "--ofile"): 
			outputfile = arg 

        if not outputfile: 
            path=inputfile.split('/')
            path[len(path)-1]=path[len(path)-1].split('.')[0]+".eps"
            outputfile="/".join(map(str,path))
        
        
	print 'Input file is ', inputfile 
        print 'Output file is ', outputfile 
        plot(inputfile,outputfile)



def plot(inputfile,outputfile):

        expected=[]
        observed=[]
        first = True
        with open(inputfile) as f:
                for line in f:
                    line=line.strip('\n')
                    if first:
                        expected=line.split(',')
                        first = False
                    else:
                        observed=line.split(',')

        size=len(expected)
        index = range(1,size+1)

	plt.plot(index, expected,'*-', linewidth=2.0, label="Expected")
	plt.plot(index, observed,'+-', linewidth=2.0, label="Observed")
                    
        # print expected
        # print observed
        plt.legend(loc='lower right')
	plt.gca().axes.get_xaxis().set_visible(False)
        plt.savefig(outputfile,format='eps')
        # plt.show()



if __name__ == "__main__": 
	main(sys.argv[1:])
