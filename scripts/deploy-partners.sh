cd ..

echo "Configure partnerclientA..."
mkdir partnerclientA
cp -r ./partnerclient/* ./partnerclientA
cp -r ./conf/partnerclientA/* ./partnerclientA

echo "Configure partnerclientB..."
mkdir partnerclientB
cp -r ./partnerclient/* ./partnerclientB
cp -r ./conf/partnerclientB/* ./partnerclientB

# Go back to scripts folder
cd scripts
