import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './cabinet-template.reducer';

export const CabinetTemplate = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const cabinetTemplateList = useAppSelector(state => state.cabinetTemplate.entities);
  const loading = useAppSelector(state => state.cabinetTemplate.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="cabinet-template-heading" data-cy="CabinetTemplateHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.home.title">Cabinet Templates</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/cabinet-template/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.home.createLabel">Create new Cabinet Template</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {cabinetTemplateList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.code">Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('category')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.category">Category</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('category')} />
                </th>
                <th className="hand" onClick={sort('defaultWidthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultWidthMm">Default Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('defaultWidthMm')} />
                </th>
                <th className="hand" onClick={sort('defaultHeightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultHeightMm">Default Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('defaultHeightMm')} />
                </th>
                <th className="hand" onClick={sort('defaultDepthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultDepthMm">Default Depth Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('defaultDepthMm')} />
                </th>
                <th className="hand" onClick={sort('minWidthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.minWidthMm">Min Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('minWidthMm')} />
                </th>
                <th className="hand" onClick={sort('maxWidthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.maxWidthMm">Max Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxWidthMm')} />
                </th>
                <th className="hand" onClick={sort('supportsDoors')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsDoors">Supports Doors</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supportsDoors')} />
                </th>
                <th className="hand" onClick={sort('supportsDrawers')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsDrawers">Supports Drawers</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supportsDrawers')} />
                </th>
                <th className="hand" onClick={sort('supportsShelves')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsShelves">Supports Shelves</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supportsShelves')} />
                </th>
                <th className="hand" onClick={sort('fusionTemplateName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.fusionTemplateName">Fusion Template Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fusionTemplateName')} />
                </th>
                <th className="hand" onClick={sort('csvProfileJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.csvProfileJson">Csv Profile Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('csvProfileJson')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('sortOrder')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.sortOrder">Sort Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sortOrder')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cabinetTemplateList.map(cabinetTemplate => (
                <tr key={`entity-${cabinetTemplate.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/cabinet-template/${cabinetTemplate.id}`} variant="link" size="sm">
                      {cabinetTemplate.id}
                    </Button>
                  </td>
                  <td>{cabinetTemplate.code}</td>
                  <td>{cabinetTemplate.name}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.CabinetCategory.${cabinetTemplate.category}`} />
                  </td>
                  <td>{cabinetTemplate.defaultWidthMm}</td>
                  <td>{cabinetTemplate.defaultHeightMm}</td>
                  <td>{cabinetTemplate.defaultDepthMm}</td>
                  <td>{cabinetTemplate.minWidthMm}</td>
                  <td>{cabinetTemplate.maxWidthMm}</td>
                  <td>{cabinetTemplate.supportsDoors ? 'true' : 'false'}</td>
                  <td>{cabinetTemplate.supportsDrawers ? 'true' : 'false'}</td>
                  <td>{cabinetTemplate.supportsShelves ? 'true' : 'false'}</td>
                  <td>{cabinetTemplate.fusionTemplateName}</td>
                  <td>{cabinetTemplate.csvProfileJson}</td>
                  <td>{cabinetTemplate.isActive ? 'true' : 'false'}</td>
                  <td>{cabinetTemplate.sortOrder}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/cabinet-template/${cabinetTemplate.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/cabinet-template/${cabinetTemplate.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/cabinet-template/${cabinetTemplate.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.home.notFound">No Cabinet Templates found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CabinetTemplate;
