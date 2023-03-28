# Installing dummy nodes
Go into the util folder

```bash
pip install -r requirements.txt
```

## Ipv8
```bash
git clone https://github.com/Tribler/py-ipv8.git pyipv8
cd pyipv8
pip install -r requirements.txt
```

## Libsodium
There is a chance you get an error for not installing Libsodium if this is the case use this guide to install
: https://py-ipv8.readthedocs.io/en/latest/preliminaries/install_libsodium.html

## Run
The following code is to run 3 dummy nodes
```bash
python community.py 3
```
