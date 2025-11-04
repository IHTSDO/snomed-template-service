#!/bin/bash

BRANCH='main'

# Directory to store the checked out resources
TRANSFORMATION_TEMPLATE_DIR=snomed-international-resources

# Remove the directory if it exists
rm -rf $TRANSFORMATION_TEMPLATE_DIR


# Clone the repository containing the resources
git clone --single-branch --branch $BRANCH https://github.com/IHTSDO/snomed-international-resources.git $TRANSFORMATION_TEMPLATE_DIR