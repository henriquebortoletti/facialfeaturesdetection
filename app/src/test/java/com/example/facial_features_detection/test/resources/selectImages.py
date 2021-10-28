from shutil import copy

sources = [0, 30, 60, 90, 120, 150, 240, 270, 300, 330, 510, 600, 690, 750, 810, 900, 960, 990,
           1050, 1080, 1110, 1200]

for i in sources:
    copy("frames/frame"+str(i)+".jpg", "selected/frames/")
