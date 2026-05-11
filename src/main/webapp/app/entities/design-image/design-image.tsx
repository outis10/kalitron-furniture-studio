import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './design-image.reducer';

export const DesignImage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const designImageList = useAppSelector(state => state.designImage.entities);
  const loading = useAppSelector(state => state.designImage.loading);

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
      <h2 id="design-image-heading" data-cy="DesignImageHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.designImage.home.title">Design Images</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.designImage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/design-image/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.designImage.home.createLabel">Create new Design Image</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {designImageList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('imageType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.imageType">Image Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('imageType')} />
                </th>
                <th className="hand" onClick={sort('fileName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.fileName">File Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileName')} />
                </th>
                <th className="hand" onClick={sort('filePath')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.filePath">File Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('filePath')} />
                </th>
                <th className="hand" onClick={sort('mimeType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.mimeType">Mime Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mimeType')} />
                </th>
                <th className="hand" onClick={sort('fileSizeKb')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.fileSizeKb">File Size Kb</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileSizeKb')} />
                </th>
                <th className="hand" onClick={sort('widthPx')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.widthPx">Width Px</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('widthPx')} />
                </th>
                <th className="hand" onClick={sort('heightPx')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.heightPx">Height Px</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heightPx')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('uploadedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.uploadedAt">Uploaded At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uploadedAt')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.designImage.session">Session</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {designImageList.map(designImage => (
                <tr key={`entity-${designImage.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/design-image/${designImage.id}`} variant="link" size="sm">
                      {designImage.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.ImageType.${designImage.imageType}`} />
                  </td>
                  <td>{designImage.fileName}</td>
                  <td>{designImage.filePath}</td>
                  <td>{designImage.mimeType}</td>
                  <td>{designImage.fileSizeKb}</td>
                  <td>{designImage.widthPx}</td>
                  <td>{designImage.heightPx}</td>
                  <td>{designImage.isActive ? 'true' : 'false'}</td>
                  <td>
                    {designImage.uploadedAt ? <TextFormat type="date" value={designImage.uploadedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{designImage.description}</td>
                  <td>
                    {designImage.session ? (
                      <Link to={`/design-session/${designImage.session.id}`}>{designImage.session.sessionCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/design-image/${designImage.id}`}
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
                        to={`/design-image/${designImage.id}/edit`}
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
                        onClick={() => (window.location.href = `/design-image/${designImage.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.home.notFound">No Design Images found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default DesignImage;
