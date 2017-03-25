#!/usr/bin/python

import sys, getopt
import matplotlib.pyplot as plt


def main(argv):
	inputfile = ''
	outputfile = ''
        title = ''
	try: 
            opts, args = getopt.getopt(argv,"hi:o:t:",["help","ifile=","ofile=","title"]) 
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
                elif opt in ("-t", "--title"):
                        title = arg

        if not outputfile: 
            path=inputfile.split('/')
            path[len(path)-1]=path[len(path)-1].split('.')[0]+".eps"
            outputfile="/".join(map(str,path))
        
        
	print 'Input file is ', inputfile 
        print 'Output file is ', outputfile 
        plot(inputfile,outputfile,title)



def plot(inputfile,outputfile, title):

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

        color = "black"
        markersize = 10
        fillstyle="none"

	plt.plot(index, expected,'^--', linewidth=0.5, label="Expected CDF",
                color=color, fillstyle=fillstyle, markersize=markersize)
	plt.plot(index, observed,'+--', linewidth=0.5, label="Observed CDF",
                color=color, fillstyle=fillstyle, markersize=markersize)
                    
        # print expected
        # print observed
        #plt.figure().suptitle(title)
        plt.title(title, fontsize=32)
        plt.ylim([0.0,1.0])
        plt.tick_params(axis='both', which='major', labelsize=23)
        plt.legend(loc='lower right', prop={'size':23})
	plt.gca().axes.get_xaxis().set_visible(False)
        plt.savefig(outputfile,format='eps')
        # plt.show()



if __name__ == "__main__": 
	main(sys.argv[1:])
