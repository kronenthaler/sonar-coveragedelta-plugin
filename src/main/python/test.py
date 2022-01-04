class Test:
    def __init__(self):
        self.x = 0

    def f(self, n):
        if n == 0 or n == 1:
            return 1
        return n * self.f(n-1)

    def uncovered(self, n, m):
        fn = self.f(n)
        fm = self.f(m)
        fnm = self.f(n-m)
        return fn / (fnm * fm)
