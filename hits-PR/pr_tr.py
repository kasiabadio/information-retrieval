import numpy as np

#L1  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L1  = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2  = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
#L3  = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
L3  = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5  = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6  = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7  = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8  = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9  = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])

ITERATIONS = 100


def iteratively_compute_pagerank_df(M, n, q = 0.15, iter = 10):
    
    # results vector, starting values 1/n
    v = [1/n] * n
    v_prev = v.copy()
    
    # M*v    
    for iterations in range(iter):
        
        v_prev = v.copy()
        for i in range(0, n):
            
            v[i] = 0
            for j in range(0, n):
                
                if M[i][j] != 0:
                    v[i] += v_prev[j] * M[i][j]
            
            v_prev[i] = 0.85 * v[i]
            v_prev[i] += 0.15
            v[i] = v_prev[i]
            
        # normalize vector, sum must be equal to 1
        v = np.array(v)
        arr1 = v / v.min()
        arr1 = arr1 / arr1.sum()
        
    return arr1
    
    
def compute_trustrank_df(M, tr, n, q = 0.15, iter = 10):
    
    # results vector, starting values 1/n
    v = [1/n] * n
    v_prev = v.copy()
    
    # M*v    
    for iterations in range(iter):
        
        v_prev = v.copy()
        for i in range(0, n):
            
            v[i] = 0
            for j in range(0, n):
                
                if M[i][j] != 0:
                    v[i] += v_prev[j] * M[i][j]
            
            v_prev[i] = 0.85 * v[i]
            v_prev[i] += 0.15 * tr[i]
            v[i] = v_prev[i]
            
        # normalize vector, sum must be equal to 1
        v = np.array(v)
        mini = np.array([el for el in v if el > 0]).min()
        arr1 = v / mini
        arr1 = arr1 / arr1.sum()
        
    return arr1


def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)
    
    ## compute the stochastic matrix M
    for i in range(0, 10):
        c[i] = sum(L[i])
    
    for i in range(0, 10):
        for j in range(0, 10):
            if L[j][i] == 0: 
                M[i][j] = 0
            else:
                M[i][j] = 1.0/c[j]
    return M
    
    
    
print("Matrix L (indices)")
print(L)    

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)

### compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
q = 0.15
pagerank = iteratively_compute_pagerank_df(M, 10, q)
pretty_pagerank = []
for i in range(len(pagerank)):
    pretty_pagerank.append((i + 1, pagerank[i]))
pretty_pagerank.sort(key=lambda x: x[1], reverse=True) 
print("PAGERANK")
for i in range(len(pagerank)):
    print(pretty_pagerank[i][0], " : ", pretty_pagerank[i][1])


pr = np.zeros([10], dtype=float)
    
### compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)


q = 0.15
d = np.zeros([10], dtype=float)
tr = [v for v in d]
tr[0] = 1 # mark pages which are good
tr[1] = 1
v = np.array(tr) # normalize them
mini = np.array([el for el in v if el > 0]).min()
arr = v / mini
arr = arr / arr.sum()

trustrank = compute_trustrank_df(M, arr, 10)
pretty_trustrank = []
for i in range(len(trustrank)):
    pretty_trustrank.append((i + 1, trustrank[i]))
pretty_trustrank.sort(key=lambda x: x[1], reverse=True) 
print("TRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")
for i in range(len(trustrank)):
    print(pretty_trustrank[i][0], " : ", pretty_trustrank[i][1])
    
### remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4) 
### before computing trustrank
M[6][2] = 0
M[4][0] = 0

q = 0.15
d = np.zeros([10], dtype=float)
tr = [v for v in d]
tr[0] = 1 # mark pages which are good
tr[1] = 1
v = np.array(tr) # normalize them
mini = np.array([el for el in v if el > 0]).min()
arr = v / mini
arr = arr / arr.sum()

trustrank = compute_trustrank_df(M, arr, 10)
pretty_trustrank = []
for i in range(len(trustrank)):
    pretty_trustrank.append((i + 1, trustrank[i]))
pretty_trustrank.sort(key=lambda x: x[1], reverse=True) 
print("TRUSTRANK AFTER REMOVAL OF 3->7 & 1->5")
for i in range(len(trustrank)):
    print(pretty_trustrank[i][0], " : ", pretty_trustrank[i][1])